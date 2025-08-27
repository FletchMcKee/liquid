// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:RequiresApi(33)

package io.github.fletchmckee.liquid.internal.shaders

import android.graphics.RenderEffect.createChainEffect
import android.graphics.RenderEffect.createRuntimeShaderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import io.github.fletchmckee.liquid.internal.LiquidScopeImpl

internal fun RuntimeShader.setLiquidUniforms(
  bounds: Rect,
  frostRadius: Float,
  cornerRadii: FloatArray,
  refraction: Float,
  curve: Float,
  edge: Float,
) {
  setFloatUniform(
    "effectRect",
    frostRadius, // left
    frostRadius, // top
    bounds.width - frostRadius, // right
    bounds.height - frostRadius, // bottom
  )
  setFloatUniform("cornerRadii", cornerRadii)
  setFloatUniform("refraction", refraction)
  setFloatUniform("curve", curve)
  setFloatUniform("edge", edge)
}

internal fun RuntimeShader.setFrostUniforms(
  bounds: Rect,
  frostRadius: Float,
  cornerRadii: FloatArray,
) {
  setFloatUniform("blurRadius", frostRadius)
  setFloatUniform(
    "effectRect",
    frostRadius, // left
    frostRadius, // top
    bounds.width - frostRadius, // right
    bounds.height - frostRadius, // bottom
  )
  setFloatUniform("cornerRadii", cornerRadii)
}

internal fun createRenderEffect(
  liquidShader: RuntimeShader,
  horizontalShader: RuntimeShader,
  verticalShader: RuntimeShader,
  bounds: Rect,
  frostRadius: Float,
  cornerRadii: FloatArray,
  reusableScope: LiquidScopeImpl,
): RenderEffect {
  liquidShader.setLiquidUniforms(
    bounds = bounds,
    frostRadius = frostRadius,
    cornerRadii = cornerRadii,
    refraction = reusableScope.refraction,
    curve = reusableScope.curve,
    edge = reusableScope.edge,
  )
  val liquidEffect = createRuntimeShaderEffect(liquidShader, "content")
  if (frostRadius <= 0f) {
    return liquidEffect.asComposeRenderEffect()
  }

  horizontalShader.setFrostUniforms(
    bounds = bounds,
    frostRadius = frostRadius,
    cornerRadii = cornerRadii,
  )

  verticalShader.setFrostUniforms(
    bounds = bounds,
    frostRadius = frostRadius,
    cornerRadii = cornerRadii,
  )

  val horizontalBlur = createRuntimeShaderEffect(horizontalShader, "content")
  val verticalBlur = createRuntimeShaderEffect(verticalShader, "content")
  val blurEffect = createChainEffect(horizontalBlur, verticalBlur)
  return createChainEffect(liquidEffect, blurEffect).asComposeRenderEffect()
}
