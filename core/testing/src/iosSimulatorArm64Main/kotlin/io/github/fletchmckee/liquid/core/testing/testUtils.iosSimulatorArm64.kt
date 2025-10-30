// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.core.testing

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runSkikoComposeUiTest
import androidx.compose.ui.unit.Density
import com.github.takahirom.roborazzi.roborazziSystemPropertyCompareOutputDirectory
import io.github.takahirom.roborazzi.CompareOptions
import io.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage

actual abstract class ScreenshotTest

actual fun runScreenshotTest(
  testName: String,
  content: @Composable (() -> Unit),
) = runSkikoComposeUiTest(
  size = Size(width = 1200f, height = 2600f),
  density = Density(3f),
) {
  setContent {
    content()
  }

  waitForIdle()
  onRoot().captureRoboImage(
    composeUiTest = this,
    filePath = "ios/$testName.png",
    roborazziOptions = RoborazziOptions(
      compareOptions = CompareOptions(
        outputDirectoryPath = roborazziSystemPropertyCompareOutputDirectory(),
      ),
    ),
  )
}
