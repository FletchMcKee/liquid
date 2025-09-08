// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.utils

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.Canvas
import coil3.Image

// Used for screenshots/benchmark tests.
//
// The liquid effect mainly works by displacing pixels near edges to other coordinates. So if we have a single
// color we can't really tell if the effect is working.
internal class GradientImage(
  private val startColor: Int,
  private val endColor: Int,
  override val width: Int = 100,
  override val height: Int = 100,
  override val size: Long = 0,
  override val shareable: Boolean = true,
) : Image {
  override fun draw(canvas: Canvas) {
    val paint = Paint().apply {
      shader = LinearGradient(
        0f, // x0
        0f, // y0
        width.toFloat(), // x1
        height.toFloat(), // y1
        startColor,
        endColor,
        Shader.TileMode.CLAMP,
      )
    }
    canvas.drawRect(
      0f, // left
      0f, // top
      width.toFloat(), // right
      height.toFloat(),
      paint, // bottom
    )
  }
}

internal val BlueRedGradient = GradientImage(startColor = Color.Blue.toArgb(), endColor = Color.Red.toArgb())
