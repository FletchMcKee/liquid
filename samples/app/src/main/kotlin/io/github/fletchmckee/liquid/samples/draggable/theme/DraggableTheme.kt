// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.draggable.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = Purple40,
  secondary = PurpleGrey40,
  tertiary = PurpleGrey80,
  surfaceVariant = Purple80,
  background = Color.DarkGray,
  onBackground = Color.White,
  onSurfaceVariant = Color(0xFF1C1B1F),
)

@Composable
fun DraggableTheme(
  content: @Composable () -> Unit,
) = MaterialTheme(
  colorScheme = DarkColorScheme,
  typography = Typography,
  content = content,
)
