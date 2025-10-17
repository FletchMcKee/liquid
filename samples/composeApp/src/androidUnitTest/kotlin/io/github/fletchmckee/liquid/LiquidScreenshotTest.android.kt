// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTestApi::class)

package io.github.fletchmckee.liquid

import android.content.ContentProvider
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollToNode
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
import io.github.fletchmckee.liquid.samples.app.demos.clock.LiquidClockScreen
import io.github.fletchmckee.liquid.samples.app.demos.drag.LiquidDraggableScreen
import io.github.fletchmckee.liquid.samples.app.demos.grid.LiquidGridScreen
import io.github.fletchmckee.liquid.samples.app.demos.many.ManyLiquidNodesScreen
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.LiquidStickyHeaderScreen
import io.github.fletchmckee.liquid.samples.app.theme.LiquidTheme
import kotlin.test.BeforeTest
import kotlin.test.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class LiquidScreenshotTest {

  @get:Rule val roborazziRule = RoborazziRule(
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
  @BeforeTest
  fun before() {
    @Suppress("UNCHECKED_CAST")
    val clazz = Class.forName("org.jetbrains.compose.resources.AndroidContextProvider") as Class<ContentProvider>
    Robolectric.setupContentProvider(clazz)

    val context = ApplicationProvider.getApplicationContext<Context>()
    val drawable = context.getDrawable(io.github.fletchmckee.liquid.samples.app.R.drawable.moon_and_stars)!!
    val bitmap = drawable.toBitmap()
    val engine = FakeImageLoaderEngine.Builder()
      .default(bitmap.asImage())
      .build()
    val imageLoader = ImageLoader.Builder(context)
      .components { add(engine) }
      .build()
    SingletonImageLoader.setUnsafe(imageLoader)
  }

  @Config(sdk = [31, 35])
  @Test
  fun capture_drag_no_frost() = runScreenshotTest {
    LiquidDraggableScreen()
  }

  @Config(sdk = [31, 35])
  @Test
  fun capture_drag_10_dp_frost() = runScreenshotTest(initialFrost = 10f) {
    LiquidDraggableScreen()
  }

  @Test fun capture_drag_half_dispersion() = runScreenshotTest(initialDispersion = 0.5f) {
    LiquidDraggableScreen()
  }

  @Test fun capture_grid_no_frost() = runScreenshotTest {
    LiquidGridScreen()
  }

  @Test fun capture_grid_10_dp_frost() = runScreenshotTest(initialFrost = 10f) {
    LiquidGridScreen()
  }

  @Test fun capture_sticky_header_no_frost_scrolled() = runScreenshotTest(
    performAction = {
      onNodeWithTag("stickyHeaderList")
        .performScrollToNode(hasTestTag("imageItem99"))
    },
    content = { LiquidStickyHeaderScreen() },
  )

  @Test fun capture_sticky_header_10_dp_frost_scrolled() = runScreenshotTest(
    initialFrost = 10f,
    performAction = {
      onNodeWithTag("stickyHeaderList")
        .performScrollToNode(hasTestTag("imageItem99"))
    },
    content = { LiquidStickyHeaderScreen() },
  )

  @Test fun capture_many_liquid_nodes_no_frost() = runScreenshotTest(darkMode = false) {
    ManyLiquidNodesScreen()
  }

  @Test fun capture_many_liquid_nodes_10_dp_frost() = runScreenshotTest(
    darkMode = false,
    initialFrost = 10f,
  ) {
    ManyLiquidNodesScreen()
  }

  @Test fun capture_clock_no_frost() = runScreenshotTest {
    LiquidClockScreen(disableAnimation = true)
  }

  @Test fun capture_clock_10_dp_frost() = runScreenshotTest(initialFrost = 10f) {
    LiquidClockScreen(disableAnimation = true)
  }

  @Test fun capture_clock_quarter_dispersion_no_frost() = runScreenshotTest(initialDispersion = 0.25f) {
    LiquidClockScreen(disableAnimation = true)
  }

  // TODO: Look into better screenshot testing options, this shouldn't be this hard.
  private fun runScreenshotTest(
    darkMode: Boolean = true,
    initialFrost: Float = 0f,
    initialDispersion: Float = 0f,
    performAction: (ComposeUiTest.() -> Unit)? = null,
    content: @Composable () -> Unit,
  ) {
    val controller = Robolectric.buildActivity(ComponentActivity::class.java)
    controller.setup()

    try {
      val environment = AndroidComposeUiTestEnvironment { controller.get() }
      environment.runTest {
        setContent {
          LiquidTheme(
            darkMode = darkMode,
            initialFrost = initialFrost,
            initialDispersion = initialDispersion,
          ) {
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
    } finally {
      controller
        .pause()
        .stop()
        .destroy()
    }
  }
}
