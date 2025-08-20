// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun Modifier.liquidBackup(
  width: Dp = 2.dp,
  cornerPercent: Int = 0,
  colors: List<Color> = listOf(FlawedWhite.copy(alpha = 0.6f), Ink.copy(alpha = 0.1f)),
  start: Offset = Offset.Zero,
  tileMode: TileMode = TileMode.Clamp,
) = this.drawBehind {
  val strokeWidth = width.toPx()
  val minDimension = minOf(size.width, size.height)
  val cornerRadius = CornerRadius(minDimension * (cornerPercent / 100f))
  drawRoundRect(
    brush = Brush.radialGradient(
      colors = colors,
      center = start,
      radius = minDimension,
      tileMode = tileMode,
    ),
    topLeft = start,
    size = size,
    cornerRadius = cornerRadius,
    style = Stroke(width = strokeWidth),
  )
}

private val FlawedWhite = Color.White
private val Ink = Color.Black
