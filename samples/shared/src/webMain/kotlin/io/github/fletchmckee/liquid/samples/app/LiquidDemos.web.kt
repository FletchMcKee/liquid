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
import io.github.fletchmckee.liquid.samples.app.demos.pulltorefresh.PullToRefresh
import io.github.fletchmckee.liquid.samples.app.demos.pulltorefresh.pullToRefreshDestination
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.stickyHeaderDestination

external interface MediaQueryList {
  val matches: Boolean
}

external interface Window {
  fun matchMedia(query: String): MediaQueryList
}

external val window: Window

@Stable
actual val DemosList: List<DemoData> = buildList {
  if (pullToRefreshEnabled) {
    add(DemoData("Pull to Refresh", PullToRefresh))
  }
  addAll(FullySupportedDemos)
}

actual fun NavGraphBuilder.platformDemoDestinations(navController: NavHostController) {
  demosListDestination(navController)
  if (pullToRefreshEnabled) {
    pullToRefreshDestination(navController)
  }
  clockDestination(navController)
  dragDestination(navController)
  gridDestination(navController)
  stickyHeaderDestination(navController)
  manyDestination(navController)
}

private inline val pullToRefreshEnabled: Boolean
  get() = window.matchMedia(query = "(pointer: coarse)").matches ||
    window.matchMedia(query = "(hover: none)").matches
