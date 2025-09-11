// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalMetricApi::class)

package io.github.fletchmckee.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LiquidBenchmark {
  @get:Rule
  val benchmarkRule = MacrobenchmarkRule()

  // Has none of the effects applied to provide a baseline comparison.
  @Test fun dragBoxBaseline() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Drag", useLiquid = false) },
    measureBlock = { dragFigureEight() },
  )

  @Test fun dragLiquidBoxNoFrost() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Drag") },
    measureBlock = { dragFigureEight() },
  )

  @Test fun dragLiquidBoxFrost10() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Drag", initialFrost = 10f) },
    measureBlock = { dragFigureEight() },
  )

  @Test fun dragFrostSlider() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Drag") },
    measureBlock = { dragFrostSlider() },
  )

  @Test fun scrollLiquidGridBaseline() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Grid", useLiquid = false) },
    measureBlock = {
      flingElementDownThenUp(
        testTag = "liquidGrid",
        gestureMargins = floatArrayOf(0.2f, 0.2f, 0.2f, 0.4f),
      )
    },
  )

  @Test fun scrollLiquidGridNoFrost() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Grid") },
    measureBlock = {
      flingElementDownThenUp(
        testTag = "liquidGrid",
        gestureMargins = floatArrayOf(0.2f, 0.2f, 0.2f, 0.4f),
      )
    },
  )

  @Test fun scrollLiquidGridFrost10() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Grid", initialFrost = 10f) },
    measureBlock = {
      flingElementDownThenUp(
        testTag = "liquidGrid",
        gestureMargins = floatArrayOf(0.2f, 0.2f, 0.2f, 0.4f),
      )
    },
  )

  @Test fun scrollLiquidStickyHeaderBaseline() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "StickyHeader", useLiquid = false) },
    measureBlock = { flingElementDownThenUp("stickyHeaderList") },
  )

  @Test fun scrollLiquidStickyHeaderNoFrost() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "StickyHeader") },
    measureBlock = { flingElementDownThenUp("stickyHeaderList") },
  )

  @Test fun scrollLiquidStickyHeaderFrost10() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "StickyHeader", initialFrost = 10f) },
    measureBlock = { flingElementDownThenUp("stickyHeaderList") },
  )

  @Test fun scrollManyLiquidNodesBaseline() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Many", useLiquid = false) },
    measureBlock = { flingElementDownThenUp("liquidNodesList") },
  )

  @Test fun scrollManyLiquidNodesNoFrost() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Many") },
    measureBlock = { flingElementDownThenUp("liquidNodesList") },
  )

  @Test fun scrollManyLiquidNodesFrost10() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = { navigateTo(startDestination = "Many", initialFrost = 10f) },
    measureBlock = { flingElementDownThenUp("liquidNodesList") },
  )

  companion object {
    const val ITERATIONS = 5
    const val PACKAGE_NAME = "io.github.fletchmckee.liquid.samples.app"
    const val START_DESTINATION = "$PACKAGE_NAME.START_DESTINATION"
    const val USE_LIQUID = "$PACKAGE_NAME.USE_LIQUID"
    const val INITIAL_FROST = "$PACKAGE_NAME.INITIAL_FROST"
  }
}
