// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RenderEffect.createBlurEffect
import android.graphics.RenderEffect.createChainEffect
import android.graphics.RenderEffect.createRuntimeShaderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionOnScreen
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState

// Android 12 and lower can't reference a RuntimeShader, so this is split into separate
// node types. Once minSdk is 33, this can be simplified to LiquidElement only.
internal actual fun liquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
): AbstractLiquidElement<out AbstractLiquidNode> = when {
  Build.VERSION.SDK_INT >= 33 -> LiquidElement(liquidState, block)
  else -> LiquidBackupElement(liquidState, block)
}

// Using `positionOnScreen()` so that dialogs/popups share the same logic as other views.
internal actual fun LayoutCoordinates.liquidPositionOnScreen(): Offset = positionOnScreen()

@RequiresApi(33)
internal class LiquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidElement<LiquidNode>(liquidState, block) {
  override fun create() = LiquidNode(liquidState, block)
}

@RequiresApi(33)
internal class LiquidNode(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidNode(liquidState, block) {
  private val liquidShader = RuntimeShader(LiquidShader)
  private var cachedBlurEffect: android.graphics.RenderEffect? = null

  override fun createRenderEffect(): RenderEffect? = with(reusableScope) {
    // We shouldn't have empty bounds at this point, but set the RenderEffect to null if we do.
    if (size.isUnspecified) return null

    liquidShader.updateLiquidUniforms()
    val liquidEffect = createRuntimeShaderEffect(liquidShader, "content")

    if (frostRadius < 1f) {
      return liquidEffect.asComposeRenderEffect()
    }

    val blurEffect = cachedBlurEffect
      ?.takeUnless { mutatedFields has Fields.Frost }
      ?: createBlurEffect(
        frostRadius,
        frostRadius,
        Shader.TileMode.CLAMP,
      ).also { cachedBlurEffect = it }

    return createChainEffect(liquidEffect, blurEffect).asComposeRenderEffect()
  }

  private fun RuntimeShader.updateLiquidUniforms() = with(reusableScope) {
    setFloatUniform("size", size.width, size.height)
    setFloatUniform("cornerRadii", cornerRadii)
    setFloatUniform("refraction", refraction)
    setFloatUniform("curve", curve)
    setFloatUniform("edge", edge)
    setColorUniform("tint", argbColor)
    setFloatUniform("saturation", saturation)
    setFloatUniform("dispersion", dispersion)
  }
}
