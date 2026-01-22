// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.fletchmckee.liquid.samples.app.demos.clock.clockDestination
import io.github.fletchmckee.liquid.samples.app.demos.drag.dragDestination
import io.github.fletchmckee.liquid.samples.app.demos.grid.gridDestination
import io.github.fletchmckee.liquid.samples.app.demos.many.manyDestination
import io.github.fletchmckee.liquid.samples.app.demos.pulltorefresh.PullToRefresh
import io.github.fletchmckee.liquid.samples.app.demos.pulltorefresh.pullToRefreshDestination
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.stickyHeaderDestination
import io.github.fletchmckee.liquid.samples.app.demos.webview.LiquidWebViewScreen
import io.github.fletchmckee.liquid.samples.app.demos.webview.WebView

@Stable
actual val DemosList: List<DemoData> = buildList {
  add(DemoData("Pull to Refresh", PullToRefresh))
  addAll(FullySupportedDemos)
  add(DemoData("WebView", WebView))
}

actual fun NavGraphBuilder.platformDemoDestinations(navController: NavHostController) {
  pullToRefreshDestination(navController)
  clockDestination(navController)
  dragDestination(navController)
  gridDestination(navController)
  stickyHeaderDestination(navController)
  manyDestination(navController)
  webViewNavigation(navController)
}

private fun NavGraphBuilder.webViewNavigation(navController: NavHostController) = composable<WebView> {
  LiquidWebViewScreen(
    navController = navController,
    modifier = Modifier.fillMaxSize(),
  )
}
