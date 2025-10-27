// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.fletchmckee.liquid.samples.app.demos.clock.Clock
import io.github.fletchmckee.liquid.samples.app.demos.drag.Drag
import io.github.fletchmckee.liquid.samples.app.demos.grid.Grid
import io.github.fletchmckee.liquid.samples.app.demos.many.Many
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.StickyHeader

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    val startDestination = intent.getStringExtra(START_DESTINATION).toStartDestination()
    val useLiquid = intent.getBooleanExtra(USE_LIQUID, true)
    val initialFrost = intent.getFloatExtra(INITIAL_FROST, 0f)
    val initialDispersion = intent.getFloatExtra(INITIAL_DISPERSION, 0f)
    val isBenchmark = intent.getBooleanExtra(IS_BENCHMARK, false)
    setContent {
      LiquidDemos(
        startDestination = startDestination,
        useLiquid = useLiquid,
        initialFrost = initialFrost,
        initialDispersion = initialDispersion,
        isBenchmark = isBenchmark,
      )
    }
  }

  // Used for benchmarks to avoid navigation muddying the results.
  private fun String?.toStartDestination(): Any {
    val startDestination = this?.let { enumValueOf<StartDestination>(it) } ?: StartDestination.DemosList
    return when (startDestination) {
      StartDestination.DemosList -> Demos
      StartDestination.Drag -> Drag
      StartDestination.Grid -> Grid
      StartDestination.StickyHeader -> StickyHeader
      StartDestination.Many -> Many
      StartDestination.Clock -> Clock
    }
  }

  private companion object {
    const val PACKAGE_NAME = "io.github.fletchmckee.liquid.samples.app"
    const val START_DESTINATION = "$PACKAGE_NAME.START_DESTINATION"
    const val USE_LIQUID = "$PACKAGE_NAME.USE_LIQUID"
    const val INITIAL_FROST = "$PACKAGE_NAME.INITIAL_FROST"
    const val INITIAL_DISPERSION = "$PACKAGE_NAME.INITIAL_DISPERSION"
    const val IS_BENCHMARK = "$PACKAGE_NAME.IS_BENCHMARK"

    enum class StartDestination { DemosList, Drag, Grid, StickyHeader, Many, Clock }
  }
}
