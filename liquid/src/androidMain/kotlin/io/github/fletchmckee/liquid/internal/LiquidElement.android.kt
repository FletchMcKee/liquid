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
import io.github.fletchmckee.liquid.internal.shaders.setFrostUniforms
import io.github.fletchmckee.liquid.internal.shaders.setLiquidUniforms

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

  override fun invalidateDrawIfNeeded() {
    if (reusableScope.mutatedFields has Fields.InvalidateFlags) {
      invalidateDraw()
    }
  }

  override fun onDetach() {
    super.onDetach()
    cachedRenderEffect = null
  }

  override fun ContentDrawScope.drawLiquidEffects(
    layer: GraphicsLayer,
    drawBlock: () -> Unit,
  ) {
    layer.renderEffect = obtainRenderEffect()
    drawBlock()
  }

  private fun obtainRenderEffect(): RenderEffect = cachedRenderEffect
    ?.takeUnless { reusableScope.mutatedFields has Fields.RenderEffectFields }
    ?: run {
      liquidShader.setLiquidUniforms(
        bounds = reusableScope.recordingBounds,
        frostRadius = reusableScope.frostRadius,
        cornerRadii = reusableScope.cornerRadii,
        refraction = reusableScope.refraction,
        curve = reusableScope.curve,
        edge = reusableScope.edge,
        argbColor = reusableScope.argbColor,
        saturation = reusableScope.saturation,
        dispersion = reusableScope.dispersion,
      )

      val liquidEffect = createRuntimeShaderEffect(liquidShader, "content")
      if (reusableScope.frostRadius < 1f) {
        return@run liquidEffect.asComposeRenderEffect()
      }

      horizontalShader.setFrostUniforms(
        bounds = reusableScope.recordingBounds,
        frostRadius = reusableScope.frostRadius,
        cornerRadii = reusableScope.cornerRadii,
      )

      verticalShader.setFrostUniforms(
        bounds = reusableScope.recordingBounds,
        frostRadius = reusableScope.frostRadius,
        cornerRadii = reusableScope.cornerRadii,
      )

      val horizontalFrost = createRuntimeShaderEffect(horizontalShader, "content")
      val verticalFrost = createRuntimeShaderEffect(verticalShader, "content")
      val blurEffect = createChainEffect(horizontalFrost, verticalFrost)
      createChainEffect(liquidEffect, blurEffect).asComposeRenderEffect()
    }.also { cachedRenderEffect = it }
}
