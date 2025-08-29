// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = LiquidGreen,
  secondary = Purple80,
  tertiary = PurpleGrey80,
  surfaceVariant = Ink50,
  background = Ink,
  onPrimary = FlawedWhite,
  onBackground = FlawedWhite,
  onSurfaceVariant = FlawedWhite,
)

@Composable
fun LiquidTheme(
  content: @Composable () -> Unit,
) = MaterialTheme(
  colorScheme = DarkColorScheme,
  typography = Typography,
  content = content,
)
