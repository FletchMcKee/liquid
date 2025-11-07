// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTestApi::class)

package io.github.fletchmckee.liquid.samples.app

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.fletchmckee.liquid.core.testing.ScreenshotTest
import io.github.fletchmckee.liquid.core.testing.runScreenshotTest
import io.github.fletchmckee.liquid.samples.app.demos.clock.LiquidClockScreen
import io.github.fletchmckee.liquid.samples.app.demos.drag.LiquidDraggableScreen
import io.github.fletchmckee.liquid.samples.app.demos.grid.LiquidGridScreen
import io.github.fletchmckee.liquid.samples.app.demos.many.ManyLiquidNodesScreen
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.LiquidStickyHeaderScreen
import io.github.fletchmckee.liquid.samples.app.theme.LiquidTheme
import kotlin.test.Test

class LiquidScreenshotTest : ScreenshotTest() {
  @Test fun capture_drag_no_frost() = runLiquidScreenshotTest(
    testName = "capture_drag_no_frost",
  ) {
    LiquidDraggableScreen()
  }

  @Test
  fun capture_drag_10_dp_frost() = runLiquidScreenshotTest(
    testName = "capture_drag_10_dp_frost",
    initialFrost = 10f,
  ) {
    LiquidDraggableScreen()
  }

  @Test fun capture_drag_half_dispersion() = runLiquidScreenshotTest(
    testName = "capture_drag_half_dispersion",
    initialDispersion = 0.5f,
  ) {
    LiquidDraggableScreen()
  }

  @Test fun capture_grid_no_frost() = runLiquidScreenshotTest(
    testName = "capture_grid_no_frost",
    darkMode = false,
  ) {
    LiquidGridScreen()
  }

  @Test fun capture_grid_10_dp_frost() = runLiquidScreenshotTest(
    testName = "capture_grid_10_dp_frost",
    darkMode = false,
    initialFrost = 10f,
  ) {
    LiquidGridScreen()
  }

  @Test fun capture_sticky_header_no_frost_scrolled() = runLiquidScreenshotTest(
    testName = "capture_sticky_header_no_frost_scrolled",
  ) {
    LiquidStickyHeaderScreen(
      listState = defaultLazyListState,
    )
  }

  @Test fun capture_sticky_header_10_dp_frost_scrolled() = runLiquidScreenshotTest(
    testName = "capture_sticky_header_10_dp_frost_scrolled",
    initialFrost = 10f,
  ) {
    LiquidStickyHeaderScreen(
      listState = defaultLazyListState,
    )
  }

  @Test fun capture_many_liquid_nodes_no_frost() = runLiquidScreenshotTest(
    testName = "capture_many_liquid_nodes_no_frost",
    darkMode = false,
    // Unlike other screens, this one adds 20f so that the default is 20.dp frost rather than 0.dp.
    initialFrost = -20f,
  ) {
    ManyLiquidNodesScreen()
  }

  @Test fun capture_many_liquid_nodes_10_dp_frost() = runLiquidScreenshotTest(
    testName = "capture_many_liquid_nodes_10_dp_frost",
    darkMode = false,
    // Unlike other screens, this one adds 20f so that the default is 20.dp frost rather than 0.dp.
    initialFrost = -10f,
  ) {
    ManyLiquidNodesScreen()
  }

  @Test fun capture_clock_no_frost() = runLiquidScreenshotTest(
    testName = "capture_clock_no_frost",
  ) {
    LiquidClockScreen()
  }

  @Test fun capture_clock_10_dp_frost() = runLiquidScreenshotTest(
    testName = "capture_clock_10_dp_frost",
    initialFrost = 10f,
  ) {
    LiquidClockScreen()
  }

  @Test fun capture_clock_quarter_dispersion_no_frost() = runLiquidScreenshotTest(
    testName = "capture_clock_quarter_dispersion_no_frost",
    initialDispersion = 0.25f,
  ) {
    LiquidClockScreen()
  }

  private fun runLiquidScreenshotTest(
    testName: String,
    darkMode: Boolean = true,
    initialFrost: Float = 0f,
    initialDispersion: Float = 0f,
    content: @Composable () -> Unit,
  ) = runScreenshotTest(
    testName = testName,
    content = {
      LiquidTheme(
        darkMode = darkMode,
        initialFrost = initialFrost,
        initialDispersion = initialDispersion,
        isScreenshotTest = true,
      ) {
        content()
      }
    },
  )

  private companion object {
    // Lets the sticky header hover over text.
    val defaultLazyListState = LazyListState(
      firstVisibleItemIndex = 99,
      firstVisibleItemScrollOffset = 1000,
    )
  }
}
