// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.grid

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Grid

fun NavGraphBuilder.gridDestination(
  useLiquid: Boolean = true,
  initialFrost: Float = 10f,
) = composable<Grid> {
  LiquidGridScreen(
    useLiquid = useLiquid,
    initialFrost = initialFrost,
    modifier = Modifier.fillMaxSize(),
  )
}
