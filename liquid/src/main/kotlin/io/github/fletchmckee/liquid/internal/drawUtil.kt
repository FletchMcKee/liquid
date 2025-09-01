// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize
import io.github.fletchmckee.liquid.Liquefiable
import kotlin.sequences.forEach

internal fun ContentDrawScope.recordLiquefiablesIntoLayer(
  layer: GraphicsLayer,
  liquefiables: List<Liquefiable>,
  bounds: Rect,
) {
  if (liquefiables.isEmpty()) return
  // We avoid unnecessary liquidScope invalidations by observing the mutableState boundsOnScreen
  // and layers here.
  layer.record(bounds.size.toIntSize()) {
    liquefiables
      .asSequence()
      // Only record content inside the effect's bounds.
      .filter { bounds.overlaps(it.boundsOnScreen) }
      .forEach { liquefiable ->
        liquefiable.layer
          ?.takeUnless { it.isReleased || it.size.isEmpty }
          ?.let { liquefiableLayer ->
            // Position content where it should appear on screen.
            val (x, y) = liquefiable.boundsOnScreen.topLeft.orZero - bounds.topLeft.orZero
            translate(x, y) {
              drawLayer(liquefiableLayer)
            }
          }
      }
  }
}

/**
 * Allows passing a [Shape] parameter to a composable that can be used for other GraphicsLayer requirements
 * along with being used in our liquid nodes.
 */
internal fun Shape.cornerRadiiPx(size: Size, density: Density): FloatArray = when (this) {
  CircleShape -> {
    val radius = size.minDimension / 2f
    floatArrayOf(radius, radius, radius, radius)
  }
  is RoundedCornerShape -> {
    // Unlike the effectRect that is LTRB, the shader's cornerRadii is quadrant based where the order is:
    // bottomEnd, topEnd, bottomStart, topStart.
    floatArrayOf(
      bottomEnd.toPx(size, density),
      topEnd.toPx(size, density),
      bottomStart.toPx(size, density),
      topStart.toPx(size, density),
    )
  }
  else -> floatArrayOf(0f, 0f, 0f, 0f)
}

internal inline val IntSize?.isEmpty: Boolean get() = when {
  this == null -> true
  width <= 0 || height <= 0 -> true
  else -> false
}

internal inline val Offset.orZero: Offset get() = takeOrElse { Offset.Zero }

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun Int.has(flag: Int): Boolean = (this and flag) != 0
