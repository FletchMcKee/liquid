// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RenderEffect.createChainEffect
import android.graphics.RenderEffect.createRuntimeShaderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.github.fletchmckee.liquid.LiquidState

@RequiresApi(33)
internal fun RuntimeShader.setLiquidUniforms(
  bounds: Rect,
  frostRadius: Float,
  cornerRadius: Float,
  refraction: Float,
  curve: Float,
  sharp: Float,
) {
  setFloatUniform(
    "effectRect",
    frostRadius, // left
    frostRadius, // top
    bounds.width - frostRadius, // right
    bounds.height - frostRadius, // bottom
  )
  setFloatUniform("cornerRadius", cornerRadius)
  setFloatUniform("refraction", refraction)
  setFloatUniform("curve", curve)
  setFloatUniform("sharp", sharp)
}

internal fun android.graphics.RenderEffect.configureRenderEffect(
  horizontalShader: RuntimeShader,
  verticalShader: RuntimeShader,
  frostRadius: Float,
  cornerRadius: Float,
  bounds: Rect,
): RenderEffect = when {
  frostRadius > 0 -> {
    horizontalShader.apply {
      setFloatUniform("blurRadius", frostRadius)
      setFloatUniform(
        "effectRect",
        frostRadius,
        frostRadius,
        bounds.width - frostRadius,
        bounds.height - frostRadius,
      )
      setFloatUniform("cornerRadius", cornerRadius)
    }

    verticalShader.apply {
      setFloatUniform("blurRadius", frostRadius)
      setFloatUniform(
        "effectRect",
        frostRadius,
        frostRadius,
        bounds.width - frostRadius,
        bounds.height - frostRadius,
      )
      setFloatUniform("cornerRadius", cornerRadius)
    }

    val horizontalBlur = createRuntimeShaderEffect(horizontalShader, "content")
    val verticalBlur = createRuntimeShaderEffect(verticalShader, "content")
    val blurEffect = createChainEffect(horizontalBlur, verticalBlur)
    createChainEffect(this, blurEffect).asComposeRenderEffect()
  }
  else -> asComposeRenderEffect()
}

internal inline val IntSize?.isEmpty: Boolean get() = when {
  this == null -> true
  width <= 0 || height <= 0 -> true
  else -> false
}

internal inline val Offset.orZero: Offset get() = takeOrElse { Offset.Zero }

/**
 * This could also be improved, but for now it helps as it allows passing a [Shape] parameter to a composable that can be used for other
 * `graphicsLayer` requirements along with being used in our [LiquidState] nodes.
 */
internal fun Shape.cornerRadiusPx(size: Size, density: Density): Float = when (this) {
  CircleShape -> size.minDimension / 2f
  is RoundedCornerShape -> {
    var topStart = topStart.toPx(size, density)
    var topEnd = topEnd.toPx(size, density)
    var bottomEnd = bottomEnd.toPx(size, density)
    var bottomStart = bottomStart.toPx(size, density)

    val minDimension = size.minDimension

    if (topStart + bottomStart > minDimension) {
      val scale = minDimension / (topStart + bottomStart)
      topStart *= scale
      bottomStart *= scale
    }

    if (topEnd + bottomEnd > minDimension) {
      val scale = minDimension / (topEnd + bottomEnd)
      topEnd *= scale
      bottomEnd *= scale
    }

    // Return average corner radius as a single representative float
    (topStart + topEnd + bottomEnd + bottomStart) / 4f
  }
  else -> 0f
}
