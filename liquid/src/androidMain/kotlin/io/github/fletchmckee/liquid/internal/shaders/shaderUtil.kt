// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:RequiresApi(33)

package io.github.fletchmckee.liquid.internal.shaders

import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Rect

internal fun RuntimeShader.setLiquidUniforms(
  bounds: Rect,
  frostRadius: Float,
  cornerRadii: FloatArray,
  refraction: Float,
  curve: Float,
  edge: Float,
  argbColor: Int,
  saturation: Float,
  dispersion: Float,
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
  setColorUniform("tint", argbColor)
  setFloatUniform("saturation", saturation)
  setFloatUniform("dispersion", dispersion)
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
