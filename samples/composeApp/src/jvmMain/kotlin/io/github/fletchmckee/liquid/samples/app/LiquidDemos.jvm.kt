// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.github.fletchmckee.liquid.samples.app.demos.clock.clockDestination
import io.github.fletchmckee.liquid.samples.app.demos.drag.dragDestination
import io.github.fletchmckee.liquid.samples.app.demos.grid.gridDestination
import io.github.fletchmckee.liquid.samples.app.demos.many.manyDestination
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.stickyHeaderDestination

@Stable
actual val DemosList: List<DemoData> = FullySupportedDemos

actual fun NavGraphBuilder.platformDemoDestinations(navController: NavHostController) {
  clockDestination(navController)
  dragDestination(navController)
  gridDestination(navController)
  stickyHeaderDestination(navController)
  manyDestination(navController)
}
