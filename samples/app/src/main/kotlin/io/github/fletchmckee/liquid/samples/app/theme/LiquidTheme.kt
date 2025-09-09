// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
  primary = LiquidPurple,
  secondary = LiquidGreen,
  secondaryContainer = LiquidLightPurple,
  tertiary = PurpleGrey80,
  surface = FlawedWhite30,
  surfaceVariant = FlawedWhite50,
  background = FlawedWhite,
  onPrimary = Ink,
  onBackground = Ink,
  onSurfaceVariant = Ink,
)
private val DarkColorScheme = darkColorScheme(
  primary = LiquidPurple,
  secondary = LiquidGreen,
  secondaryContainer = LiquidLightPurple,
  tertiary = PurpleGrey80,
  surface = Ink30,
  surfaceVariant = Ink50,
  background = Ink,
  onPrimary = FlawedWhite,
  onBackground = FlawedWhite,
  onSurfaceVariant = FlawedWhite,
)

@Composable
fun LiquidTheme(
  darkMode: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) = MaterialTheme(
  colorScheme = if (darkMode) DarkColorScheme else LightColorScheme,
  typography = Typography,
  content = content,
)
