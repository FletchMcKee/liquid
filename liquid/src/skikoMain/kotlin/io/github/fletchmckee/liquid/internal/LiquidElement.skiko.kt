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
import androidx.compose.ui.util.fastRoundToInt
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.internal.shaders.LiquidShader
import org.jetbrains.skia.FilterTileMode
import org.jetbrains.skia.IRect
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

internal actual fun liquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
): AbstractLiquidElement<out AbstractLiquidNode> = LiquidElement(liquidState, block)

internal actual fun LayoutCoordinates.liquidPositionOnScreen(): Offset = positionInWindow()

internal class LiquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidElement<LiquidNode>(liquidState, block) {
  override fun create() = LiquidNode(liquidState, block)
}

internal class LiquidNode(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidNode(liquidState, block) {
  private val liquidShader = RuntimeShaderBuilder(RuntimeEffect.makeForShader(LiquidShader))
  private var cachedRenderEffect: RenderEffect? = null
  private var cachedBlurImageFilter: ImageFilter? = null

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
    val blurEffect = if (reusableScope.sigma > 0f) {
      cachedBlurImageFilter?.takeUnless { reusableScope.mutatedFields has Fields.BlurEffectFields }
        ?: ImageFilter.makeBlur(
          sigmaX = reusableScope.sigma,
          sigmaY = reusableScope.sigma,
          mode = FilterTileMode.CLAMP,
          crop = with(reusableScope) {
            val frostInt = frostRadius.fastRoundToInt()
            IRect.makeLTRB(
              l = frostInt,
              t = frostInt,
              r = recordingBounds.width.fastRoundToInt() - frostInt,
              b = recordingBounds.height.fastRoundToInt() - frostInt,
            )
          },
        )
    } else {
      null
    }.also { cachedBlurImageFilter = it }

    return ImageFilter.makeRuntimeShader(
      runtimeShaderBuilder = liquidShader,
      shaderNames = arrayOf("content"),
      inputs = arrayOf(blurEffect),
    ).asComposeRenderEffect()
  }

  private fun RuntimeShaderBuilder.updateLiquidUniforms() = with(reusableScope) {
    uniform(
      "effectRect",
      frostRadius, // left
      frostRadius, // top
      recordingBounds.width - frostRadius, // right
      recordingBounds.height - frostRadius, // bottom
    )
    uniform("cornerRadii", cornerRadii)
    uniform("refraction", refraction)
    uniform("curve", curve)
    uniform("edge", edge)
    uniform("tint", colorComponents)
    uniform("saturation", saturation)
    uniform("dispersion", dispersion)
  }
}
