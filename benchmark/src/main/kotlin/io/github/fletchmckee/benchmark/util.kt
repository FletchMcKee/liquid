// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.benchmark

import androidx.test.uiautomator.UiDevice

internal fun UiDevice.dragFigureEight(
  repetitions: Int = 2,
  steps: Int = 25,
) = repeat(repetitions) {
  val centerX = displayWidth / 2
  val centerY = displayHeight / 2
  val upY = (displayHeight * 0.2f).toInt()
  val downY = (displayHeight * 0.8f).toInt()
  val leftX = (displayWidth * 0.2f).toInt()
  val rightX = (displayWidth * 0.8f).toInt()

  swipe(centerX, centerY, rightX, downY, steps)
  waitForIdle()

  swipe(rightX, downY, leftX, downY, steps)
  waitForIdle()

  swipe(leftX, downY, rightX, upY, steps)
  waitForIdle()

  swipe(rightX, upY, leftX, upY, steps)
  waitForIdle()

  swipe(leftX, upY, centerX, centerY, steps)
  waitForIdle()
}
