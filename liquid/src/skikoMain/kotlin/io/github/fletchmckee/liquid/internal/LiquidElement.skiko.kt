// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import org.jetbrains.skia.FilterTileMode
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

internal actual fun liquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
): AbstractLiquidElement<out AbstractLiquidNode> = LiquidElement(liquidState, block)

/**
 * Using `positionOnScreen()` results in incorrect positioning. Will need to monitor if this
 * changes in the future.
 */
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
  private var cachedBlurImageFilter: ImageFilter? = null

  override fun createRenderEffect(): RenderEffect? = with(reusableScope) {
    // We shouldn't have empty bounds at this point, but set the RenderEffect to null if we do.
    if (size.isUnspecified) return null

    liquidShader.updateLiquidUniforms()
    val blurEffect = when {
      sigma > 0f ->
        cachedBlurImageFilter
          ?.takeUnless { mutatedFields has Fields.Frost }
          ?: ImageFilter.makeBlur(
            sigmaX = sigma,
            sigmaY = sigma,
            mode = FilterTileMode.CLAMP,
          )
      else -> null
    }.also { cachedBlurImageFilter = it }

    // Logic differs from Android slightly as we can set the blurEffect as an input.
    return ImageFilter.makeRuntimeShader(
      runtimeShaderBuilder = liquidShader,
      shaderNames = arrayOf("content"),
      inputs = arrayOf(blurEffect),
    ).asComposeRenderEffect()
  }

  private fun RuntimeShaderBuilder.updateLiquidUniforms() = with(reusableScope) {
    uniform("size", size.width, size.height)
    uniform("cornerRadii", cornerRadii)
    uniform("refraction", refraction)
    uniform("curve", curve)
    uniform("edge", edge)
    uniform("tint", colorComponents)
    uniform("saturation", saturation)
    uniform("dispersion", dispersion)
  }
}
