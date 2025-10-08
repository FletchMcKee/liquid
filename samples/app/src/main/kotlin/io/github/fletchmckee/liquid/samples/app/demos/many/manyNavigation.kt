// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.many

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Many

fun NavGraphBuilder.manyDestination() = composable<Many> {
  ManyLiquidNodesScreen(
    modifier = Modifier.fillMaxSize(),
  )
}
