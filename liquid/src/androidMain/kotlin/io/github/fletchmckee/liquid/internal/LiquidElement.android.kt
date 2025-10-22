// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RenderEffect.createChainEffect
import android.graphics.RenderEffect.createRuntimeShaderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.node.invalidateDraw
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.internal.shaders.HorizontalFrostShader
import io.github.fletchmckee.liquid.internal.shaders.LiquidShader
import io.github.fletchmckee.liquid.internal.shaders.VerticalFrostShader

internal actual fun liquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
): AbstractLiquidElement<out AbstractLiquidNode> = when {
  Build.VERSION.SDK_INT >= 33 -> LiquidElement(liquidState, block)
  else -> LiquidBackupElement(liquidState, block)
}

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
  private val horizontalShader = RuntimeShader(HorizontalFrostShader)
  private val verticalShader = RuntimeShader(VerticalFrostShader)
  private var cachedRenderEffect: RenderEffect? = null
  private var cachedBlurEffect: android.graphics.RenderEffect? = null

  override fun invalidateDrawIfNeeded() {
    if (reusableScope.mutatedFields has Fields.InvalidateFlags) {
      if (reusableScope.mutatedFields has Fields.RenderEffectFields) {
        cachedRenderEffect = createRenderEffect()
      }
      invalidateDraw()
    }
  }

  override fun ContentDrawScope.applyLiquidEffects(
    layer: GraphicsLayer,
    drawBlock: () -> Unit,
  ) {
    layer.renderEffect = cachedRenderEffect
    drawBlock()
  }

  private fun createRenderEffect(): RenderEffect? {
    if (reusableScope.recordingBounds.isEmpty) return null
    liquidShader.updateLiquidUniforms()
    val liquidEffect = createRuntimeShaderEffect(liquidShader, "content")

    if (reusableScope.frostRadius < 1f) {
      cachedBlurEffect = null
      return liquidEffect.asComposeRenderEffect()
    }

    val blurEffect = cachedBlurEffect
      ?.takeUnless { reusableScope.mutatedFields has Fields.BlurEffectFields }
      ?: run {
        horizontalShader.updateFrostUniforms()
        verticalShader.updateFrostUniforms()
        val horizontalFrost = createRuntimeShaderEffect(horizontalShader, "content")
        val verticalFrost = createRuntimeShaderEffect(verticalShader, "content")
        createChainEffect(horizontalFrost, verticalFrost)
      }.also { cachedBlurEffect = it }

    return createChainEffect(liquidEffect, blurEffect).asComposeRenderEffect()
  }

  private fun RuntimeShader.updateLiquidUniforms() = with(reusableScope) {
    setFloatUniform(
      "effectRect",
      frostRadius, // left
      frostRadius, // top
      recordingBounds.width - frostRadius, // right
      recordingBounds.height - frostRadius, // bottom
    )
    setFloatUniform("cornerRadii", cornerRadii)
    setFloatUniform("refraction", refraction)
    setFloatUniform("curve", curve)
    setFloatUniform("edge", edge)
    setColorUniform("tint", argbColor)
    setFloatUniform("saturation", saturation)
    setFloatUniform("dispersion", dispersion)
  }

  private fun RuntimeShader.updateFrostUniforms() = with(reusableScope) {
    setFloatUniform("blurRadius", frostRadius)
    setFloatUniform(
      "effectRect",
      frostRadius, // left
      frostRadius, // top
      recordingBounds.width - frostRadius, // right
      recordingBounds.height - frostRadius, // bottom
    )
    setFloatUniform("cornerRadii", cornerRadii)
  }
}
