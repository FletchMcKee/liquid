// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import io.github.fletchmckee.liquid.samples.app.demos.many.ManyLiquidNodesScreen
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.LiquidStickyHeaderScreen
import io.github.fletchmckee.liquid.samples.app.theme.LiquidTheme
import io.github.fletchmckee.liquid.samples.app.utils.BlueRedGradient
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
        recordOptions = RoborazziOptions.RecordOptions(
          resizeScale = 0.5,
        ),
      ),
    ),
  )

  @DelicateCoilApi
  @Before fun before() {
    // Avoids network requests and async responses altering which images are set.
    val engine = FakeImageLoaderEngine.Builder()
      .default(BlueRedGradient)
      .build()
    val imageLoader = ImageLoader.Builder(ApplicationProvider.getApplicationContext())
      .components { add(engine) }
      .build()
    SingletonImageLoader.setUnsafe(imageLoader)
  }

  @Test fun capture_drag_no_frost() = runScreenshotTest {
    LiquidDraggableScreen()
  }

  @Test fun capture_drag_10_dp_frost() = runScreenshotTest {
    LiquidDraggableScreen(initialFrost = 10f)
  }

  @Test fun capture_grid_no_frost() = runScreenshotTest {
    LiquidGridScreen()
  }

  @Test fun capture_grid_10_dp_frost() = runScreenshotTest {
    LiquidGridScreen(initialFrost = 10f)
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

  @Test fun capture_sticky_header_no_frost_scrolled() {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = true) {
          LiquidStickyHeaderScreen()
        }
      }

      waitForIdle()
      // Scrolls to the bottom.
      onNodeWithTag("stickyHeaderList").performScrollToNode(hasTestTag("imageItem99"))
      waitForIdle()
      onRoot().captureRoboImage()
    }
  }

  @Test fun capture_sticky_header_10_dp_frost_scrolled() {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = true) {
          LiquidStickyHeaderScreen(initialFrost = 10f)
        }
      }

      waitForIdle()
      // Scrolls to the bottom.
      onNodeWithTag("stickyHeaderList").performScrollToNode(hasTestTag("imageItem99"))
      waitForIdle()
      onRoot().captureRoboImage()
    }
  }

  @Test fun capture_many_liquid_nodes_no_frost() = runScreenshotTest(darkMode = false) {
    ManyLiquidNodesScreen()
  }

  @Test fun capture_many_liquid_nodes_10_dp_frost() = runScreenshotTest(darkMode = false) {
    ManyLiquidNodesScreen(initialFrost = 10f)
  }

  private fun runScreenshotTest(
    darkMode: Boolean = true,
    content: @Composable () -> Unit,
  ) {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = darkMode) {
          content()
        }
      }

      waitForIdle()
      onRoot().captureRoboImage()
    }
  }
}
