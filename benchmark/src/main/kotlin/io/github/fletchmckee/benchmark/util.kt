// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.benchmark

import android.graphics.Point
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

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
  timeoutMs: Long = 2_000,
  speed: Int = 1_000,
  iterations: Int = 2,
) = repeat(iterations) {
  device.wait(Until.hasObject(By.res("frostSlider")), timeoutMs)
  val frostSlider = requireNotNull(device.findObject(By.res("frostSlider"))) {
    "frostSlider not found"
  }

  device.wait(Until.hasObject(By.res("frostThumb")), timeoutMs)
  val thumb = requireNotNull(device.findObject(By.res("frostThumb"))) {
    "frostThumb not found"
  }

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
