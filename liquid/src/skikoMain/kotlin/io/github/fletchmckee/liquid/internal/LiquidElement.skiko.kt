// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.node.invalidateDraw
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.internal.shaders.HorizontalFrostShader
import io.github.fletchmckee.liquid.internal.shaders.LiquidShader
import io.github.fletchmckee.liquid.internal.shaders.VerticalFrostShader
import io.github.fletchmckee.liquid.internal.shaders.setFrostUniforms
import io.github.fletchmckee.liquid.internal.shaders.setLiquidUniforms
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

internal actual fun liquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
): AbstractLiquidElement = LiquidElement(liquidState, block)

internal actual fun LayoutCoordinates.liquidPositionOnScreen(): Offset = positionInWindow()

internal class LiquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidElement(liquidState, block) {
  override fun create() = LiquidNode(liquidState, block)

  override fun update(node: AbstractLiquidNode) {
    node as LiquidNode
    node.liquidState = liquidState
    node.block = block
    node.invalidateLiquidBlock()
  }
}

internal class LiquidNode(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidNode(liquidState, block) {
  private val liquidShader = RuntimeShaderBuilder(RuntimeEffect.makeForShader(LiquidShader))
  private val horizontalShader = RuntimeShaderBuilder(RuntimeEffect.makeForShader(HorizontalFrostShader))
  private val verticalShader = RuntimeShaderBuilder(RuntimeEffect.makeForShader(VerticalFrostShader))

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

  private fun obtainRenderEffect(): RenderEffect = cachedRenderEffect?.takeUnless { reusableScope.mutatedFields has Fields.RenderEffectFields }
    ?: createRenderEffect().also { cachedRenderEffect = it }

  private fun createRenderEffect(): RenderEffect {
    liquidShader.setLiquidUniforms(
      bounds = reusableScope.recordingBounds,
      frostRadius = reusableScope.frostRadius,
      cornerRadii = reusableScope.cornerRadii,
      refraction = reusableScope.refraction,
      curve = reusableScope.curve,
      edge = reusableScope.edge,
      colorComponents = reusableScope.colorComponents,
      saturation = reusableScope.saturation,
      dispersion = reusableScope.dispersion,
    )

    val liquidImageFilter = ImageFilter.makeRuntimeShader(
      runtimeShaderBuilder = liquidShader,
      shaderNames = arrayOf("content"),
      inputs = arrayOf(null),
    )

    if (reusableScope.frostRadius < 1f) {
      return liquidImageFilter.asComposeRenderEffect()
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

    val horizontalFrost = ImageFilter.makeRuntimeShader(
      runtimeShaderBuilder = horizontalShader,
      shaderNames = arrayOf("content"),
      inputs = arrayOf(null),
    )

    val verticalFrost = ImageFilter.makeRuntimeShader(
      runtimeShaderBuilder = verticalShader,
      shaderNames = arrayOf("content"),
      inputs = arrayOf(null),
    )

    val blurEffect = ImageFilter.makeCompose(verticalFrost, horizontalFrost)
    return ImageFilter.makeCompose(liquidImageFilter, blurEffect).asComposeRenderEffect()
  }
}
