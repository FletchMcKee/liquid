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
  content: @Composable () -> Unit,
) = runSkikoComposeUiTest(
  size = Size(width = 800f, height = 1600f),
  density = Density(2f),
) {
  setContent {
    content()
  }

  waitForIdle()
  onRoot().captureRoboImage(
    composeUiTest = this,
    filePath = "ios/${roboOutputName()}.png",
    roborazziOptions = RoborazziOptions(
      compareOptions = CompareOptions(
        outputDirectoryPath = roborazziSystemPropertyCompareOutputDirectory(),
      ),
    ),
  )
}

// This is a hack, but preferable to manual specifications.
private fun roboOutputName(): String = buildString {
  // Full mangled pattern is $ClassName$test$0$$FUNCTION_REFERENCE_FOR$methodName$
  val pattern = Regex("\\$([a-zA-Z0-9_]+)\\\$test\\$0\\$\\\$FUNCTION_REFERENCE_FOR\\$([a-zA-Z0-9_]+)\\$")
  val stackString = Exception().stackTraceToString()
  val match = requireNotNull(pattern.find(stackString)) {
    "No matching class/function name was found for Roborazzi test"
  }

  append(match.groupValues[1]) // Class name
  append(".")
  append(match.groupValues[2]) // Method name
}
