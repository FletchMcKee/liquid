// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("demos") // Makes wasm/js urls more readable.
data object Demos

fun NavGraphBuilder.demosListDestination(
  navController: NavController,
) = composable<Demos> {
  Demos(navController)
}
