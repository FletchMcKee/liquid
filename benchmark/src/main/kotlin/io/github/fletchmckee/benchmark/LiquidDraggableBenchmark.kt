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
class LiquidDraggableBenchmark {
  @get:Rule
  val benchmarkRule = MacrobenchmarkRule()

  // Has none of the effects applied to provide a baseline comparison.
  @Test
  fun dragBoxBaseline() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = {
      startActivityAndWait { intent ->
        intent.putExtra(DEMO_TYPE, "Drag")
        intent.putExtra(USE_LIQUID, false)
      }
      device.waitForIdle()
    },
    measureBlock = { dragFigureEight() },
  )

  @Test
  fun dragGlassBoxNoFrost() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = {
      startActivityAndWait { intent ->
        intent.putExtra(DEMO_TYPE, "Drag")
        intent.putExtra(INITIAL_FROST, 0f)
      }
      device.waitForIdle()
    },
    measureBlock = { dragFigureEight() },
  )

  @Test
  fun dragGlassBoxFrost10() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = {
      startActivityAndWait { intent ->
        intent.putExtra(DEMO_TYPE, "Drag")
        intent.putExtra(INITIAL_FROST, 10f)
      }
      device.waitForIdle()
    },
    measureBlock = { dragFigureEight() },
  )

  @Test
  fun dragFrostSlider() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = {
      startActivityAndWait { intent ->
        intent.putExtra(DEMO_TYPE, "Drag")
        intent.putExtra(INITIAL_FROST, 0f)
      }
      device.waitForIdle()
    },
    measureBlock = { dragFrostSlider() },
  )

  @Test
  fun scrollLiquidGridBaseline() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = {
      startActivityAndWait { intent ->
        intent.putExtra(DEMO_TYPE, "Grid")
        intent.putExtra(USE_LIQUID, false)
      }
      device.waitForIdle()
    },
    measureBlock = { flingElementDownThenUp("liquidGrid") },
  )

  @Test
  fun scrollLiquidGridNoFrost() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = {
      startActivityAndWait { intent ->
        intent.putExtra(DEMO_TYPE, "Grid")
        intent.putExtra(INITIAL_FROST, 0f)
      }
      device.waitForIdle()
    },
    measureBlock = { flingElementDownThenUp("liquidGrid") },
  )

  @Test
  fun scrollLiquidGridFrost10() = benchmarkRule.measureRepeated(
    packageName = PACKAGE_NAME,
    metrics = listOf(FrameTimingMetric()),
    iterations = ITERATIONS,
    compilationMode = CompilationMode.DEFAULT,
    startupMode = StartupMode.WARM,
    setupBlock = {
      startActivityAndWait { intent ->
        intent.putExtra(DEMO_TYPE, "Grid")
        intent.putExtra(INITIAL_FROST, 10f)
      }
      device.waitForIdle()
    },
    measureBlock = { flingElementDownThenUp("liquidGrid") },
  )

  private companion object Companion {
    const val ITERATIONS = 15
    const val PACKAGE_NAME = "io.github.fletchmckee.liquid.samples.draggable"
    const val DEMO_TYPE = "$PACKAGE_NAME.DEMO_TYPE"
    const val USE_LIQUID = "$PACKAGE_NAME.USE_LIQUID"
    const val INITIAL_FROST = "$PACKAGE_NAME.INITIAL_FROST"
  }
}
