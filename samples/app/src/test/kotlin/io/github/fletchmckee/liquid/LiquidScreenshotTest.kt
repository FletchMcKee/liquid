// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.asImage
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
    // This could cause memory issues, but the gradient images are mostly useless for screenshot tests.
    val context = ApplicationProvider.getApplicationContext<Context>()
    val drawable = context.getDrawable(io.github.fletchmckee.liquid.samples.app.R.drawable.moon_and_stars)!!
    val bitmap = drawable.toBitmap()
    // Avoids network requests and async responses altering which images are set.
    val engine = FakeImageLoaderEngine.Builder()
      .default(bitmap.asImage())
      .build()
    val imageLoader = ImageLoader.Builder(ApplicationProvider.getApplicationContext())
      .components { add(engine) }
      .build()
    SingletonImageLoader.setUnsafe(imageLoader)
  }

  @Test fun capture_drag_no_frost() = runScreenshotTest(
    performAction = {
      onNodeWithTag("liquidDraggableBox")
        .performTouchInput {
          val dragAmountPx = with(density) { 150.dp.toPx() }
          swipeUp(
            startY = centerY,
            endY = centerY - dragAmountPx,
          )
        }
    },
    content = { LiquidDraggableScreen() },
  )

  @Test fun capture_drag_10_dp_frost() = runScreenshotTest(
    performAction = {
      onNodeWithTag("liquidDraggableBox")
        .performTouchInput {
          val dragAmountPx = with(density) { 150.dp.toPx() }
          swipeUp(
            startY = centerY,
            endY = centerY - dragAmountPx,
          )
        }
    },
    content = { LiquidDraggableScreen(initialFrost = 10f) },
  )

  @Test fun capture_grid_no_frost() = runScreenshotTest {
    LiquidGridScreen()
  }

  @Test fun capture_grid_10_dp_frost() = runScreenshotTest {
    LiquidGridScreen(initialFrost = 10f)
  }

  @Test fun capture_grid_no_frost_scrolled() = runScreenshotTest(
    performAction = {
      onNodeWithTag("liquidGrid")
        .performScrollToNode(hasTestTag("imageGrid99"))
    },
    content = { LiquidGridScreen() },
  )

  @Test fun capture_grid_10_dp_frost_scrolled() = runScreenshotTest(
    performAction = {
      onNodeWithTag("liquidGrid")
        .performScrollToNode(hasTestTag("imageGrid99"))
    },
    content = { LiquidGridScreen(initialFrost = 10f) },
  )

  @Test fun capture_sticky_header_no_frost_scrolled() = runScreenshotTest(
    performAction = {
      onNodeWithTag("stickyHeaderList")
        .performScrollToNode(hasTestTag("imageItem99"))
    },
    content = { LiquidStickyHeaderScreen() },
  )

  @Test fun capture_sticky_header_10_dp_frost_scrolled() = runScreenshotTest(
    performAction = {
      onNodeWithTag("stickyHeaderList")
        .performScrollToNode(hasTestTag("imageItem99"))
    },
    content = { LiquidStickyHeaderScreen(initialFrost = 10f) },
  )

  @Test fun capture_many_liquid_nodes_no_frost() = runScreenshotTest(darkMode = false) {
    ManyLiquidNodesScreen()
  }

  @Test fun capture_many_liquid_nodes_10_dp_frost() = runScreenshotTest(darkMode = false) {
    ManyLiquidNodesScreen(initialFrost = 10f)
  }

  private fun runScreenshotTest(
    darkMode: Boolean = true,
    performAction: (ComposeTestRule.() -> Unit)? = null,
    content: @Composable () -> Unit,
  ) {
    rule.apply {
      setContent {
        LiquidTheme(darkMode = darkMode) {
          content()
        }
      }

      waitForIdle()
      performAction?.let {
        performAction()
        waitForIdle()
      }
      onRoot().captureRoboImage()
    }
  }
}
