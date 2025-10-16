// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:RequiresApi(33)

package io.github.fletchmckee.liquid.internal.shaders

import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Rect
import org.jetbrains.skia.RuntimeShaderBuilder

internal fun RuntimeShaderBuilder.setLiquidUniforms(
  bounds: Rect,
  frostRadius: Float,
  cornerRadii: FloatArray,
  refraction: Float,
  curve: Float,
  edge: Float,
  colorComponents: FloatArray,
  saturation: Float,
  dispersion: Float,
) {
  uniform(
    "effectRect",
    frostRadius, // left
    frostRadius, // top
    bounds.width - frostRadius, // right
    bounds.height - frostRadius, // bottom
  )
  uniform("cornerRadii", cornerRadii)
  uniform("refraction", refraction)
  uniform("curve", curve)
  uniform("edge", edge)
  uniform("tint", colorComponents)
  uniform("saturation", saturation)
  uniform("dispersion", dispersion)
}

internal fun RuntimeShaderBuilder.setFrostUniforms(
  bounds: Rect,
  frostRadius: Float,
  cornerRadii: FloatArray,
) {
  uniform("blurRadius", frostRadius)
  uniform(
    "effectRect",
    frostRadius, // left
    frostRadius, // top
    bounds.width - frostRadius, // right
    bounds.height - frostRadius, // bottom
  )
  uniform("cornerRadii", cornerRadii)
}
