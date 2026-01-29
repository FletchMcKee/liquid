// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.video

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Video

fun NavGraphBuilder.videoDestination(
  content: @Composable () -> Unit,
) = composable<Video> {
  LiquidVideoPlayer(
    modifier = Modifier.fillMaxSize(),
  ) {
    content()
  }
}
