// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.core.testing

import androidx.compose.runtime.Composable

actual abstract class ScreenshotTest

actual fun runScreenshotTest(
  content: @Composable () -> Unit,
) = Unit
