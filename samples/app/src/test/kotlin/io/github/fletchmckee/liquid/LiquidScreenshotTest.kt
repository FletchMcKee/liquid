// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ColorImage
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.test.FakeImageLoaderEngine
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.fletchmckee.liquid.samples.app.demos.drag.LiquidDraggableScreen
import io.github.fletchmckee.liquid.samples.app.demos.grid.LiquidGridScreen
import io.github.fletchmckee.liquid.samples.app.theme.LiquidTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class LiquidScreenshotTest {
  @get:Rule val rule = createComposeRule()

  @get:Rule
  val roborazziRule = RoborazziRule(
    options = RoborazziRule.Options(
      roborazziOptions = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(
          changeThreshold = 0.01f, // 1% accepted difference
        ),
      ),
    ),
  )

  @DelicateCoilApi
  @Before fun before() {
    // Avoid network requests and async responses altering which images are set.
    val engine = FakeImageLoaderEngine.Builder()
      .default(ColorImage(Color.Blue.toArgb()))
      .build()
    val imageLoader = ImageLoader.Builder(ApplicationProvider.getApplicationContext())
      .components { add(engine) }
      .build()
    SingletonImageLoader.setUnsafe(imageLoader)
  }

  @Test fun capture_drag_no_frost() {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = true) {
          LiquidDraggableScreen()
        }
      }

      waitForIdle()
      onRoot().captureRoboImage()
    }
  }

  @Test fun capture_drag_10_dp_frost() {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = true) {
          LiquidDraggableScreen(initialFrost = 10f)
        }
      }

      waitForIdle()
      onRoot().captureRoboImage()
    }
  }

  @Test fun capture_grid_no_frost() {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = true) {
          LiquidGridScreen()
        }
      }

      waitForIdle()
      onRoot().captureRoboImage()
    }
  }

  @Test fun capture_grid_10_dp_frost() {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = true) {
          LiquidGridScreen(initialFrost = 10f)
        }
      }

      waitForIdle()
      onRoot().captureRoboImage()
    }
  }

  @Test fun capture_grid_no_frost_scrolled() {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = true) {
          LiquidGridScreen()
        }
      }

      waitForIdle()
      // Scrolls to the bottom.
      onNodeWithTag("liquidGrid").performScrollToNode(hasTestTag("imageGrid99"))
      waitForIdle()
      onRoot().captureRoboImage()
    }
  }

  @Test fun capture_grid_10_dp_frost_scrolled() {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = true) {
          LiquidGridScreen(initialFrost = 10f)
        }
      }

      waitForIdle()
      // Scrolls to the bottom.
      onNodeWithTag("liquidGrid").performScrollToNode(hasTestTag("imageGrid99"))
      waitForIdle()
      onRoot().captureRoboImage()
    }
  }
}
