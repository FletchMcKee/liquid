// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.Liquefiable
import io.github.fletchmckee.liquid.LiquidScope

// These fields are configured internally so we don't expose them as public API.
internal interface InternalLiquidScope : LiquidScope {
  var size: Size
  var positionOnScreen: Offset
  var liquefiables: List<Liquefiable>
}

// Helper class for managing LiquidScope changes.
internal class LiquidScopeImpl : InternalLiquidScope {
  internal var mutatedFields = 0

  override var frost: Dp = 0.dp
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Frost
        field = value
      }
    }

  override var shape: Shape = RectangleShape
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Shape
        field = value
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

  override var size: Size = Size.Unspecified
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Size
        field = value
      }
    }

  override var positionOnScreen: Offset = Offset.Zero
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.PositionOnScreen
        field = value
      }
    }

  override var liquefiables: List<Liquefiable> = emptyList()
    set(value) {
      if (field != value) {
        mutatedFields = mutatedFields or Fields.Liquefiables
        field = value
      }
    }

  internal fun reset() {
    frost = 0.dp
    shape = RectangleShape
    refraction = 0.25f
    curve = 0.25f
    edge = 0f
    size = Size.Unspecified
    positionOnScreen = Offset.Zero
    liquefiables = emptyList()
    // Keep this last.
    mutatedFields = 0
  }

  internal fun paddedBounds(padding: Float = 0f): Rect {
    // If size is unspecified, returning Rect.Zero will prevent the effect from being drawn.
    if (size.isUnspecified) return Rect.Zero

    return Rect(
      left = positionOnScreen.x - padding,
      top = positionOnScreen.y - padding,
      right = positionOnScreen.x + size.width + padding,
      bottom = positionOnScreen.y + size.height + padding,
    )
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

  // These don't require updating the RenderEffect, but they do require invalidating the draw.
  const val PositionOnScreen: Int = 0b1 shl 6
  const val Liquefiables: Int = 0b1 shl 7

  // PositionOnScreen isn't a shader uniform as it's only used to translate liquefiables into the correct space.
  const val RenderEffectFields: Int =
    Frost or
      Shape or
      Refraction or
      Curve or
      Edge or
      Size

  const val InvalidateFlags: Int =
    RenderEffectFields or
      PositionOnScreen or
      Liquefiables
}
