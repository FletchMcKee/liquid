// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.stickyheader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object StickyHeader

fun NavGraphBuilder.stickyHeaderDestination(
  useLiquid: Boolean = true,
  initialFrost: Float = 10f,
) = composable<StickyHeader> {
  LiquidStickyHeaderScreen(
    useLiquid = useLiquid,
    initialFrost = initialFrost,
    modifier = Modifier.fillMaxSize(),
  )
}
