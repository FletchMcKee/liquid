package io.github.fletchmckee.liquid.core.testing

import androidx.compose.runtime.Composable

actual abstract class ScreenshotTest

actual fun runScreenshotTest(
  testName: String,
  content: @Composable (() -> Unit)
) = Unit
