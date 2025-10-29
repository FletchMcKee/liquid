// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

typealias FloatRange = ClosedFloatingPointRange<Float>

@Composable
fun PrimarySlider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  valueRange: FloatRange = 0f..30f,
  steps: Int = 29,
  thumbTestTag: String = "thumb",
) = Slider(
  value = value,
  onValueChange = onValueChange,
  steps = steps,
  valueRange = valueRange,
  thumb = {
    Box(
      Modifier
        .size(32.dp)
        .padding(6.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary)
        .testTag(thumbTestTag),
    )
  },
  track = { state ->
    SliderDefaults.Track(
      sliderState = state,
      drawStopIndicator = null,
      thumbTrackGapSize = 2.dp,
      drawTick = { _, _ -> },
      modifier = Modifier.height(8.dp),
    )
  },
  modifier = modifier,
)
