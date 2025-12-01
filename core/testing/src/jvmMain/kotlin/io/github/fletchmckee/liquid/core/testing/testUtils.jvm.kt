// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.core.testing

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runSkikoComposeUiTest
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.roboOutputName
import io.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.test.TestResult

actual abstract class ScreenshotTest

actual fun runScreenshotTest(
  content: @Composable () -> Unit,
): TestResult = runSkikoComposeUiTest {
  setContent {
    content()
  }

  waitForIdle()
  onRoot().captureRoboImage(
    filePath = "jvm/${roboOutputName()}.png",
    roborazziOptions = RoborazziOptions(
      compareOptions = RoborazziOptions.CompareOptions(
        changeThreshold = 0.005f,
      ),
    ),
  )
}
