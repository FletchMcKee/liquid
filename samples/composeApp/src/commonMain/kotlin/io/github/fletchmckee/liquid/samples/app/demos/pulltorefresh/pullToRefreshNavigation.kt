// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.pulltorefresh

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("pull-to-refresh") // For wasm/js url legibility.
data object PullToRefresh

fun NavGraphBuilder.pullToRefreshDestination(navController: NavController) = composable<PullToRefresh> {
  LiquidPullToRefresh(
    navController = navController,
    modifier = Modifier.fillMaxSize(),
  )
}
