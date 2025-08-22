// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.draggable.utils

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.thenIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier = if (condition) this.block() else this

fun Modifier.blendMode(blendMode: BlendMode): Modifier = this.drawWithCache {
  val layer = obtainGraphicsLayer()
  layer.apply {
    record { drawContent() }
    this.colorFilter = ColorFilter.colorMatrix(
      ColorMatrix().apply {
        setToSaturation(0f)
      },
    )
    this.blendMode = blendMode
  }

  onDrawWithContent {
    drawLayer(layer)
  }
}

fun Modifier.safeShadow(
  elevation: Dp = 4.dp,
  shape: Shape = RoundedCornerShape(25.dp),
) = this then when {
  Build.VERSION.SDK_INT >= 33 -> Modifier.shadow(elevation, shape)
  else -> Modifier
}
