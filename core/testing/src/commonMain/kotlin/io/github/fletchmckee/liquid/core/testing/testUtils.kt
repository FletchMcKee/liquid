// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.core.testing

import androidx.compose.runtime.Composable

expect abstract class ScreenshotTest()

expect fun runScreenshotTest(
  testName: String,
  content: @Composable () -> Unit,
)
