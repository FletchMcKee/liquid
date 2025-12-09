// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTestApi::class)

package io.github.fletchmckee.liquid.samples.app

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
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
  @Test fun capture_drag_no_frost() = runLiquidScreenshotTest {
    LiquidDraggableScreen()
  }

  @Test
  fun capture_drag_10_dp_frost() = runLiquidScreenshotTest(
    initialFrost = 10f,
  ) {
    LiquidDraggableScreen()
  }

  @Test fun capture_drag_half_dispersion() = runLiquidScreenshotTest(
    initialDispersion = 0.5f,
  ) {
    LiquidDraggableScreen()
  }

  @Test fun capture_grid_no_frost() = runLiquidScreenshotTest(
    darkMode = false,
  ) {
    LiquidGridScreen(gridState = scrolledLazyGridState)
  }

  @Test fun capture_sticky_header_no_frost_scrolled() = runLiquidScreenshotTest {
    LiquidStickyHeaderScreen(listState = scrolledLazyListState)
  }

  @Test fun capture_sticky_header_10_dp_frost_scrolled() = runLiquidScreenshotTest(
    initialFrost = 10f,
  ) {
    LiquidStickyHeaderScreen(listState = scrolledLazyListState)
  }

  @Test fun capture_many_liquid_nodes_20_dp_frost() = runLiquidScreenshotTest(
    darkMode = false,
  ) {
    ManyLiquidNodesScreen()
  }

  @Test fun capture_clock_no_frost() = runLiquidScreenshotTest {
    LiquidClockScreen()
  }

  @Test fun capture_clock_10_dp_frost() = runLiquidScreenshotTest(
    initialFrost = 10f,
  ) {
    LiquidClockScreen()
  }

  @Test fun capture_clock_quarter_dispersion_no_frost() = runLiquidScreenshotTest(
    initialDispersion = 0.25f,
  ) {
    LiquidClockScreen()
  }

  private fun runLiquidScreenshotTest(
    darkMode: Boolean = true,
    initialFrost: Float = 0f,
    initialDispersion: Float = 0f,
    content: @Composable () -> Unit,
  ) = runScreenshotTest(
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
    val scrolledLazyListState = LazyListState(
      firstVisibleItemIndex = 99,
      // Lets the sticky header hover over text.
      firstVisibleItemScrollOffset = 900,
    )

    val scrolledLazyGridState = LazyGridState(
      firstVisibleItemIndex = 19,
      firstVisibleItemScrollOffset = 400,
    )
  }
}
