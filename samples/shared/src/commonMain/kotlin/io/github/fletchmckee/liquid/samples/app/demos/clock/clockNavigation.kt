// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.clock

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("clock") // Makes wasm/js urls more readable.
data object Clock

fun NavGraphBuilder.clockDestination(navController: NavController) = composable<Clock> {
  LiquidClockScreen(
    navController = navController,
    modifier = Modifier.fillMaxSize(),
  )
}
