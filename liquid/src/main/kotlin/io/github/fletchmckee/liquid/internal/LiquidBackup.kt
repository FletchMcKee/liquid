// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun Modifier.liquidBackup(
  width: Dp = 2.dp,
  shape: Shape = RectangleShape,
  colors: List<Color> = listOf(FlawedWhite.copy(alpha = 0.6f), Ink.copy(alpha = 0.1f), FlawedWhite.copy(alpha = 0.6f)),
  start: Offset = Offset.Zero,
  end: Offset = Offset.Infinite,
  tileMode: TileMode = TileMode.Clamp,
) = this.drawBehind {
  val strokeWidth = width.toPx()
  val outline = shape.createOutline(size, layoutDirection, this)
  drawOutline(
    outline = outline,
    brush = Brush.linearGradient(
      colors = colors,
      start = start,
      end = end,
      tileMode = tileMode,
    ),
    style = Stroke(width = strokeWidth),
  )
}

private val Ink = Color(0xFF111111)
private val FlawedWhite = Color(0xFFFFFBFE)
