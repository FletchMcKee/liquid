// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.core.testing

import android.content.ContentProvider
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [31, 35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
actual abstract class ScreenshotTest

actual fun runScreenshotTest(
  testName: String,
  content: @Composable () -> Unit,
) {
  @Suppress("UNCHECKED_CAST")
  val clazz = Class.forName("org.jetbrains.compose.resources.AndroidContextProvider") as Class<ContentProvider>
  Robolectric.setupContentProvider(clazz)

  val controller = Robolectric.buildActivity(ComponentActivity::class.java)
  controller.setup()

  try {
    val environment = AndroidComposeUiTestEnvironment { controller.get() }
    environment.runTest {
      setContent {
        content()
      }

      waitForIdle()
      onRoot().captureRoboImage(
        filePath = "android/$testName[${android.os.Build.VERSION.SDK_INT}].png",
        roborazziOptions = RoborazziOptions(
          compareOptions = RoborazziOptions.CompareOptions(
            changeThreshold = 0.01f,
          ),
        ),
      )
    }
  } finally {
    controller
      .pause()
      .stop()
      .destroy()
  }
}
