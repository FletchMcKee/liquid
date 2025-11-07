// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalMetricApi::class)

package io.github.fletchmckee.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LiquidBenchmark {
  @get:Rule val benchmarkRule = MacrobenchmarkRule()

  @Test fun dragBoxBaseline() = runBenchmarkTest(
    iterations = 1, // Baselines are only for insight, just run it once.
    setupBlock = { navigateTo(startDestination = "Drag", useLiquid = false) },
    measureBlock = { dragFigureEight() },
  )

  @Test fun dragLiquidBoxNoFrost() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "Drag") },
    measureBlock = { dragFigureEight() },
  )

  @Test fun dragLiquidBoxFrost10dp() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "Drag", initialFrost = 10f) },
    measureBlock = { dragFigureEight() },
  )

  @Test fun dragFrostSlider() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "Drag") },
    measureBlock = { dragFrostSlider() },
  )

  @Test fun scrollLiquidGridBaseline() = runBenchmarkTest(
    iterations = 1, // Baselines are only for insight, just run it once.
    setupBlock = { navigateTo(startDestination = "Grid", useLiquid = false) },
    measureBlock = {
      flingElementDownThenUp(
        testTag = "liquidGrid",
        flings = 1,
        gestureMargins = floatArrayOf(0.2f, 0.2f, 0.2f, 0.4f),
      )
    },
  )

  @Test fun scrollLiquidGridNoFrost() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "Grid") },
    measureBlock = {
      flingElementDownThenUp(
        testTag = "liquidGrid",
        flings = 1,
        gestureMargins = floatArrayOf(0.2f, 0.2f, 0.2f, 0.4f),
      )
    },
  )

  @Test fun scrollLiquidGridFrost10dp() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "Grid", initialFrost = 10f) },
    measureBlock = {
      flingElementDownThenUp(
        testTag = "liquidGrid",
        flings = 1,
        gestureMargins = floatArrayOf(0.2f, 0.2f, 0.2f, 0.4f),
      )
    },
  )

  @Test fun scrollLiquidStickyHeaderBaseline() = runBenchmarkTest(
    iterations = 1, // Baselines are only for insight, just run it once.
    setupBlock = { navigateTo(startDestination = "StickyHeader", useLiquid = false) },
    measureBlock = { flingElementDownThenUp("stickyHeaderList") },
  )

  @Test fun scrollLiquidStickyHeaderNoFrost() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "StickyHeader") },
    measureBlock = { flingElementDownThenUp("stickyHeaderList") },
  )

  @Test fun scrollLiquidStickyHeaderFrost10dp() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "StickyHeader", initialFrost = 10f) },
    measureBlock = { flingElementDownThenUp("stickyHeaderList") },
  )

  @Test fun scrollManyLiquidNodesBaseline() = runBenchmarkTest(
    iterations = 1, // Baselines are only for insight, just run it once.
    setupBlock = { navigateTo(startDestination = "Many", useLiquid = false) },
    measureBlock = { flingElementDownThenUp("liquidNodesList") },
  )

  @Test fun scrollManyLiquidNodesNoFrost() = runBenchmarkTest(
    // Unlike other screens, this one adds 20f so that the default is 20.dp frost rather than 0.dp.
    setupBlock = { navigateTo(startDestination = "Many", initialFrost = -20f) },
    measureBlock = { flingElementDownThenUp("liquidNodesList") },
  )

  @Test fun scrollManyLiquidNodesFrost10dp() = runBenchmarkTest(
    // Unlike other screens, this one adds 20f so that the default is 20.dp frost rather than 0.dp.
    setupBlock = { navigateTo(startDestination = "Many", initialFrost = -10f) },
    measureBlock = { flingElementDownThenUp("liquidNodesList") },
  )

  @Test fun rotatingClockBaseline() = runBenchmarkTest(
    iterations = 1, // Baselines are only for insight, just run it once.
    setupBlock = { navigateTo(startDestination = "Clock", useLiquid = false) },
    measureBlock = {
      Thread.sleep(5000)
      device.waitForIdle()
    },
  )

  @Test fun rotatingClockNoFrost() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "Clock") },
    measureBlock = {
      Thread.sleep(5000)
      device.waitForIdle()
    },
  )

  @Test fun rotatingClockFrost10dp() = runBenchmarkTest(
    setupBlock = { navigateTo(startDestination = "Clock", initialFrost = 10f) },
    measureBlock = {
      Thread.sleep(5000)
      device.waitForIdle()
    },
  )

  private fun runBenchmarkTest(
    metrics: List<Metric> = listOf(FrameTimingMetric()),
    iterations: Int = ITERATIONS,
    compilationMode: CompilationMode = CompilationMode.DEFAULT,
    startupMode: StartupMode? = StartupMode.WARM,
    setupBlock: MacrobenchmarkScope.() -> Unit,
    measureBlock: MacrobenchmarkScope.() -> Unit,
  ) = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = metrics,
    iterations = iterations,
    compilationMode = compilationMode,
    startupMode = startupMode,
    setupBlock = setupBlock,
    measureBlock = measureBlock,
  )

  companion object {
    const val ITERATIONS = 5
    const val PACKAGE_NAME = "io.github.fletchmckee.liquid.samples.app"
    const val START_DESTINATION = "$PACKAGE_NAME.START_DESTINATION"
    const val USE_LIQUID = "$PACKAGE_NAME.USE_LIQUID"
    const val INITIAL_FROST = "$PACKAGE_NAME.INITIAL_FROST"
    const val IS_BENCHMARK = "$PACKAGE_NAME.IS_BENCHMARK"
  }
}
