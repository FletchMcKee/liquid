// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LiquidVideoPlayer(
  modifier: Modifier = Modifier,
  backgroundColor: Color = MaterialTheme.colorScheme.background,
  content: @Composable () -> Unit,
) = Box(
  modifier
    .fillMaxSize()
    .background(backgroundColor),
) {
  content()
}
