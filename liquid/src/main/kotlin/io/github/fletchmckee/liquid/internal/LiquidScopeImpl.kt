// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RenderEffect.createChainEffect
import android.graphics.RenderEffect.createRuntimeShaderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.Liquefiable
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.internal.shaders.setFrostUniforms
import io.github.fletchmckee.liquid.internal.shaders.setLiquidUniforms

// These fields are configured internally so we don't expose them as public API, but they have to be set externally.
internal interface InternalLiquidScope : LiquidScope {
  var density: Density
  var size: Size
  var positionOnScreen: Offset
  var liquefiables: List<Liquefiable>
}

internal class LiquidScopeImpl : InternalLiquidScope {
  internal var mutatedFields = 0

  override var frost: Dp = 0.dp
    set(value) {
      if (field != value) {
        field = value
        // The pixel value is what gets passed to the shader, so the mutatedFields is tracked there.
        frostRadius = with(density) { value.toPx() }
      }
    }

  override var shape: Shape = CircleShape
    set(value) {
      if (field != value) {
        field = value
        // Similar to tint, we don't really care about the shape interface, we just need the corner radii,
        // so the mutatedFields tracker is set there.
        if (size.isSpecified) {
          cornerRadii = value.cornerRadiiPx(size, density)
        }
      }
    }

  override var refraction: Float = 0.25f
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Refraction
        field = value
      }
    }

  override var curve: Float = 0.25f
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Curve
        field = value
      }
    }

  override var edge: Float = 0f
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Edge
        field = value
      }
    }

  override var tint: Color = Color.Unspecified
    set(value) {
      if (field != value) {
        field = value
        // We don't set the mutatedFields here but instead in argbColor. We also avoid unnecessary invalidations by
        // doing so since Color.Transparent != Color.Unspecified, but their ARGB Int values are equal.
        argbColor = value.toArgb()
      }
    }

  override var density: Density = Density(1f)
    set(value) {
      if (field != value) {
        field = value
        frostRadius = with(value) { frost.toPx() }
      }
    }

  override var size: Size = Size.Unspecified
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Size
        field = value
        if (value.isSpecified) {
          cornerRadii = shape.cornerRadiiPx(value, density)
          paddedBounds = computePaddedBounds()
        }
      }
    }

  override var positionOnScreen: Offset = Offset.Zero
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.PositionOnScreen
        field = value
        if (value.isSpecified) {
          paddedBounds = computePaddedBounds()
        }
      }
    }

  override var liquefiables: List<Liquefiable> = emptyList()
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Liquefiables
        field = value
      }
    }

  internal var cornerRadii: FloatArray = CornerRadiiZero
    private set(value) {
      if (!field.contentEquals(value)) {
        mutatedFields = mutatedFields or Fields.Shape
        field = value
      }
    }

  internal var frostRadius: Float = 0f
    private set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Frost
        field = value
        paddedBounds = computePaddedBounds()
      }
    }

  // This internal property exists so that we call `toArgb()` only when the tint changes and not when other
  // unrelated properties change.
  internal var argbColor: Int = 0 // Same as Color.Unspecified.toArgb()
    private set(value) {
      if (field != value) {
        // We set the mutatedFields on this internal property rather than the public `tint` property because
        // ultimately this is the value we pass to the shader.
        mutatedFields = mutatedFields or Fields.Tint
        field = value
      }
    }

  internal var paddedBounds: Rect = Rect.Zero
    private set

  // Cached to avoid expensive JNI calls and native allocations on every draw.
  // Only recreated when shader uniforms change (see Fields.RenderEffectFields).
  internal var renderEffect: RenderEffect? = null

  internal fun reset() {
    mutatedFields = 0
  }

  internal fun computePaddedBounds(): Rect {
    // If size or position is unspecified, returning Rect.Zero will prevent the effect from being drawn.
    if (size.isUnspecified || positionOnScreen.isUnspecified) return Rect.Zero

    return Rect(
      left = positionOnScreen.x - frostRadius,
      top = positionOnScreen.y - frostRadius,
      right = positionOnScreen.x + size.width + frostRadius,
      bottom = positionOnScreen.y + size.height + frostRadius,
    )
  }

  @RequiresApi(33)
  internal fun obtainRenderEffect(
    liquidShader: RuntimeShader,
    horizontalShader: RuntimeShader,
    verticalShader: RuntimeShader,
  ): RenderEffect = renderEffect?.takeUnless { mutatedFields has Fields.RenderEffectFields } ?: run {
    liquidShader.setLiquidUniforms(
      bounds = paddedBounds,
      frostRadius = frostRadius,
      cornerRadii = cornerRadii,
      refraction = refraction,
      curve = curve,
      edge = edge,
      argbColor = argbColor,
    )

    val liquidEffect = createRuntimeShaderEffect(liquidShader, "content")
    if (frostRadius < 1f) {
      return@run liquidEffect.asComposeRenderEffect()
    }

    horizontalShader.setFrostUniforms(
      bounds = paddedBounds,
      frostRadius = frostRadius,
      cornerRadii = cornerRadii,
    )

    verticalShader.setFrostUniforms(
      bounds = paddedBounds,
      frostRadius = frostRadius,
      cornerRadii = cornerRadii,
    )

    val horizontalFrost = createRuntimeShaderEffect(horizontalShader, "content")
    val verticalFrost = createRuntimeShaderEffect(verticalShader, "content")
    val blurEffect = createChainEffect(horizontalFrost, verticalFrost)
    createChainEffect(liquidEffect, blurEffect).asComposeRenderEffect()
  }.also { renderEffect = it }

  companion object {
    @Stable
    internal val CornerRadiiZero = floatArrayOf(0f, 0f, 0f, 0f)
  }
}

@Suppress("ConstPropertyName")
internal object Fields {
  // A change in these requires recreating the RenderEffect and invalidating the draw.
  const val Frost: Int = 0b1
  const val Shape: Int = 0b1 shl 1
  const val Refraction: Int = 0b1 shl 2
  const val Curve: Int = 0b1 shl 3
  const val Edge: Int = 0b1 shl 4
  const val Size: Int = 0b1 shl 5
  const val Tint: Int = 0b1 shl 6

  // These don't require updating the RenderEffect, but they do require invalidating the draw.
  const val PositionOnScreen: Int = 0b1 shl 7
  const val Liquefiables: Int = 0b1 shl 8

  // PositionOnScreen isn't a shader uniform as it's only used to translate liquefiables into the correct space.
  const val RenderEffectFields: Int =
    Frost or
      Shape or
      Refraction or
      Curve or
      Edge or
      Size or
      Tint

  const val InvalidateFlags: Int =
    RenderEffectFields or
      PositionOnScreen or
      Liquefiables

  // //////////////////////////
  // Remove once minSdk is 33.
  // //////////////////////////
  const val PreTiramisuInvalidateFlags: Int =
    Frost or
      Shape or
      Edge or
      Size or
      Tint or
      PositionOnScreen or
      Liquefiables

  // //////////////////////////
  // Remove once minSdk is 31.
  // //////////////////////////
  const val PreSnowConeInvalidateFlags: Int =
    Shape or
      Edge or
      Size or
      Tint or
      PositionOnScreen or
      Liquefiables
}
