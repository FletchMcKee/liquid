// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.fletchmckee.liquid.samples.app.demos.drag.Drag
import io.github.fletchmckee.liquid.samples.app.demos.grid.Grid
import io.github.fletchmckee.liquid.samples.app.demos.many.Many
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.StickyHeader
import io.github.fletchmckee.liquid.samples.app.theme.LiquidTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val startDestination = intent.getStringExtra(START_DESTINATION).toStartDestination()
    val useLiquid = intent.getBooleanExtra(USE_LIQUID, true)
    val initialFrost = intent.getFloatExtra(INITIAL_FROST, 0f)
    val initialDispersion = intent.getFloatExtra(INITIAL_DISPERSION, 0f)
    setContent {
      LiquidTheme(
        useLiquid = useLiquid,
        initialFrost = initialFrost,
        initialDispersion = initialDispersion,
      ) {
        LiquidDemos(
          startDestination = startDestination,
          useLiquid = useLiquid,
          initialFrost = initialFrost,
        )
      }
    }
  }

  // Used for benchmarks to avoid navigation muddying the results.
  private fun String?.toStartDestination(): Any {
    val startDestination = this?.let { enumValueOf<StartDestination>(it) } ?: StartDestination.DemosList
    return when (startDestination) {
      StartDestination.DemosList -> DemosList
      StartDestination.Drag -> Drag
      StartDestination.Grid -> Grid
      StartDestination.StickyHeader -> StickyHeader
      StartDestination.Many -> Many
    }
  }

  private companion object {
    const val PACKAGE_NAME = "io.github.fletchmckee.liquid.samples.app"
    const val START_DESTINATION = "$PACKAGE_NAME.START_DESTINATION"
    const val USE_LIQUID = "$PACKAGE_NAME.USE_LIQUID"
    const val INITIAL_FROST = "$PACKAGE_NAME.INITIAL_FROST"
    const val INITIAL_DISPERSION = "$PACKAGE_NAME.INITIAL_DISPERSION"

    enum class StartDestination { DemosList, Drag, Grid, StickyHeader, Many }
  }
}
