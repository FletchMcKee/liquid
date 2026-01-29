// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue

@Stable
internal class LiquidScopeManager(
  initialFrost: Float = 0f,
  initialRefraction: Float = 0.25f,
  initialCurve: Float = 0.25f,
  initialEdge: Float = 0f,
  initialSaturation: Float = 1f,
  initialCornerPercent: Int = 25,
  initialDispersion: Float = 0f,
  initialContrast: Float = 1f,
) {
  var frost by mutableFloatStateOf(initialFrost)
  var refraction by mutableFloatStateOf(initialRefraction)
  var curve by mutableFloatStateOf(initialCurve)
  var edge by mutableFloatStateOf(initialEdge)
  var saturation by mutableFloatStateOf(initialSaturation)
  var cornerPercent by mutableIntStateOf(initialCornerPercent)
  var dispersion by mutableFloatStateOf(initialDispersion)
  var contrast by mutableFloatStateOf(initialContrast)
}

@Composable
internal fun retainLiquidScopeManager(
  initialFrost: Float = 0f,
  initialRefraction: Float = 0.25f,
  initialCurve: Float = 0.25f,
  initialEdge: Float = 0f,
  initialSaturation: Float = 1f,
  initialCornerPercent: Int = 25,
  initialDispersion: Float = 0f,
  initialContrast: Float = 1f,
) = retain {
  LiquidScopeManager(
    initialFrost = initialFrost,
    initialRefraction = initialRefraction,
    initialCurve = initialCurve,
    initialEdge = initialEdge,
    initialSaturation = initialSaturation,
    initialCornerPercent = initialCornerPercent,
    initialDispersion = initialDispersion,
    initialContrast = initialContrast,
  )
}
