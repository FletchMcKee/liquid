// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package io.github.fletchmckee.liquid.samples.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
  primary = LiquidPurple,
  secondary = LiquidGreen,
  secondaryContainer = LiquidLightPurple,
  tertiary = PurpleGrey80,
  surface = FlawedWhite30,
  surfaceVariant = FlawedWhite50,
  surfaceTint = LiquidPurple,
  background = FlawedWhite,
  onPrimary = Ink,
  onBackground = Ink,
  onSurfaceVariant = Ink,
  surfaceContainer = Silver,
)
private val DarkColorScheme = darkColorScheme(
  primary = LiquidPurple,
  secondary = LiquidGreen,
  secondaryContainer = LiquidLightPurple,
  tertiary = PurpleGrey80,
  surface = Ink30,
  surfaceVariant = Ink50,
  surfaceTint = LiquidPurple,
  background = Ink,
  onPrimary = FlawedWhite,
  onBackground = FlawedWhite,
  onSurfaceVariant = FlawedWhite,
  surfaceContainer = Stone,
)

internal val LiquidShadow = Shadow(radius = 4.dp, color = Color.Black.copy(alpha = 0.5f))

internal val LocalUseLiquid = staticCompositionLocalOf { true }
internal val LocalInitialFrost = staticCompositionLocalOf { 0f }
internal val LocalInitialDispersion = staticCompositionLocalOf { 0f }
internal val LocalIsBenchmark = staticCompositionLocalOf { false }
internal val LocalIsScreenshotTest = staticCompositionLocalOf { false }

@Composable
fun LiquidTheme(
  darkMode: Boolean = isSystemInDarkTheme(),
  useLiquid: Boolean = true,
  initialFrost: Float = 0f,
  initialDispersion: Float = 0f,
  isBenchmark: Boolean = false,
  isScreenshotTest: Boolean = false,
  content: @Composable () -> Unit,
) = CompositionLocalProvider(
  LocalUseLiquid provides useLiquid,
  LocalInitialFrost provides initialFrost,
  LocalInitialDispersion provides initialDispersion,
  LocalIsBenchmark provides isBenchmark,
  LocalIsScreenshotTest provides isScreenshotTest,
) {
  MaterialTheme(
    colorScheme = if (darkMode) DarkColorScheme else LightColorScheme,
    typography = Typography,
    content = content,
  )
}
