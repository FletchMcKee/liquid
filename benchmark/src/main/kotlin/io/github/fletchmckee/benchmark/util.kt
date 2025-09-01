// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.benchmark

import android.graphics.Point
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import io.github.fletchmckee.benchmark.LiquidBenchmark.Companion.INITIAL_FROST
import io.github.fletchmckee.benchmark.LiquidBenchmark.Companion.START_DESTINATION
import io.github.fletchmckee.benchmark.LiquidBenchmark.Companion.USE_LIQUID

internal fun MacrobenchmarkScope.waitForObject(
  testTag: String,
  timeout: Long = 2_000,
): UiObject2 {
  device.wait(Until.hasObject(By.res(testTag)), timeout)
  return requireNotNull(device.findObject(By.res(testTag))) { "$testTag not found" }
}

internal fun MacrobenchmarkScope.navigateTo(
  startDestination: String,
  useLiquid: Boolean = true,
  initialFrost: Float = 0f,
) {
  startActivityAndWait { intent ->
    intent.putExtra(START_DESTINATION, startDestination)
    intent.putExtra(USE_LIQUID, useLiquid)
    intent.putExtra(INITIAL_FROST, initialFrost)
  }

  device.waitForIdle()
}

internal fun MacrobenchmarkScope.dragFigureEight(
  repetitions: Int = 2,
  steps: Int = 25,
) = repeat(repetitions) {
  val centerX = device.displayWidth / 2
  val centerY = device.displayHeight / 2
  val upY = (device.displayHeight * 0.2f).toInt()
  val downY = (device.displayHeight * 0.8f).toInt()
  val leftX = (device.displayWidth * 0.2f).toInt()
  val rightX = (device.displayWidth * 0.8f).toInt()

  device.swipe(centerX, centerY, rightX, downY, steps)
  device.waitForIdle()

  device.swipe(rightX, downY, leftX, downY, steps)
  device.waitForIdle()

  device.swipe(leftX, downY, rightX, upY, steps)
  device.waitForIdle()

  device.swipe(rightX, upY, leftX, upY, steps)
  device.waitForIdle()

  device.swipe(leftX, upY, centerX, centerY, steps)
  device.waitForIdle()
}

internal fun MacrobenchmarkScope.dragFrostSlider(
  timeout: Long = 2_000,
  speed: Int = 1_000,
  iterations: Int = 4,
) = repeat(iterations) {
  val frostSlider = waitForObject("frostSlider", timeout)
  val thumb = waitForObject("frostThumb", timeout)

  val track = frostSlider.visibleBounds
  val y = track.centerY()
  val start = Point(track.left, y)
  val end = Point(track.right, y)

  // Begins in the center
  thumb.drag(end, speed)
  device.waitForIdle()

  // Drag back to start.
  thumb.drag(start, speed)
  device.waitForIdle()
}

fun MacrobenchmarkScope.flingElementDownThenUp(
  testTag: String,
  timeout: Long = 2_000,
) {
  val element = waitForObject(testTag, timeout)
  val horizontalMargin = device.displayWidth / 5
  val bottomMargin = device.displayHeight / 3
  // The Slider interferes with the fling action, so setting a larger bottom margin to
  // avoid missed scrolls.
  element.setGestureMargins(
    horizontalMargin,
    0,
    horizontalMargin,
    bottomMargin,
  )

  element.fling(Direction.DOWN)
  element.fling(Direction.DOWN)
  device.waitForIdle()

  element.fling(Direction.UP)
  element.fling(Direction.UP)
  device.waitForIdle()
}
