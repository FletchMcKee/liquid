// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.github.fletchmckee.liquid.Liquid

internal inline val IntSize?.isEmpty: Boolean get() = when {
  this == null -> true
  width <= 0 || height <= 0 -> true
  else -> false
}

internal inline val Offset.orZero: Offset get() = takeOrElse { Offset.Zero }

/**
 * This could also be improved, but for now it helps as it allows passing a [Shape] parameter to a composable that can be used for other
 * `graphicsLayer` requirements along with being used in our [Liquid] nodes.
 */
internal fun Shape.cornerRadiusPx(size: Size, density: Density): Float = when (this) {
  CircleShape -> size.minDimension / 2f
  is RoundedCornerShape -> {
    var topStart = topStart.toPx(size, density)
    var topEnd = topEnd.toPx(size, density)
    var bottomEnd = bottomEnd.toPx(size, density)
    var bottomStart = bottomStart.toPx(size, density)

    val minDimension = size.minDimension

    if (topStart + bottomStart > minDimension) {
      val scale = minDimension / (topStart + bottomStart)
      topStart *= scale
      bottomStart *= scale
    }

    if (topEnd + bottomEnd > minDimension) {
      val scale = minDimension / (topEnd + bottomEnd)
      topEnd *= scale
      bottomEnd *= scale
    }

    // Return average corner radius as a single representative float
    (topStart + topEnd + bottomEnd + bottomStart) / 4f
  }
  else -> 0f
}
