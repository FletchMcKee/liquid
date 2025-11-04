// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import io.github.fletchmckee.liquid.internal.LiquidScopeImpl.Companion.Float4Zero

internal fun ContentDrawScope.recordLiquefiablesIntoLayer(
  layer: GraphicsLayer,
  reusableScope: InternalLiquidScope,
) = with(reusableScope) {
  // Only record content inside the effect's bounds.
  val liquefiables = liquefiables.fastFilter { boundsInRoot.overlaps(it.boundsOnScreen) }
  if (liquefiables.isEmpty()) return@with
  // We avoid unnecessary liquidScope invalidations by observing the mutableState boundsOnScreen
  // and layers here. Changes to these properties will recompose the full draw pass.
  layer.record(size.toIntSize()) {
    liquefiables.fastForEach { liquefiable ->
      liquefiable.layer
        ?.takeUnless { it.isReleased }
        ?.let { liquefiableLayer ->
          // Position content where it should appear on screen.
          val (x, y) = liquefiable.boundsOnScreen.topLeft.orZero - positionOnScreen.orZero
          withTransform(
            {
              rotate(degrees = inverseRotationZ, pivot = Offset.Zero)
              scale(scaleX = inverseScaleX, scaleY = inverseScaleY, pivot = Offset.Zero)
              translate(left = x, top = y)
            },
          ) {
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
@androidx.annotation.Size(value = 4)
internal fun Shape.cornerRadiiPx(size: Size, density: Density): FloatArray = when (this) {
  CircleShape -> {
    floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f)
  }
  is RoundedCornerShape -> {
    if (size.minDimension <= 0) {
      Float4Zero
    } else {
      // Unlike the effectRect that is LTRB, the shader's cornerRadii is quadrant based where the order is:
      // bottomEnd, topEnd, bottomStart, topStart.
      floatArrayOf(
        bottomEnd.toPx(size, density) / size.minDimension,
        topEnd.toPx(size, density) / size.minDimension,
        bottomStart.toPx(size, density) / size.minDimension,
        topStart.toPx(size, density) / size.minDimension,
      )
    }
  }
  else -> Float4Zero
}

internal inline val IntSize?.isEmpty: Boolean get() = when {
  this == null -> true
  width <= 0 || height <= 0 -> true
  else -> false
}

internal inline val Offset.orZero: Offset get() = takeOrElse { Offset.Zero }

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun Int.has(flag: Int): Boolean = (this and flag) != 0

internal fun Outline.asPath(): Path = when (this) {
  is Outline.Rectangle -> Path().apply { addRect(rect) }
  is Outline.Rounded -> Path().apply { addRoundRect(roundRect) }
  is Outline.Generic -> path
}

// ///////////////
// Backup effects
// ///////////////

// This won't be that accurate, but we should at least provide an edge-like inner border using gradients
// if the user provided a value.
internal fun ContentDrawScope.drawBackupEdgeEffect(shapePath: Path) = clipPath(shapePath) {
  val strokeWidth = 4.dp.toPx()
  val radius = size.minDimension
  // Light at topLeft corner
  drawPath(
    path = shapePath,
    brush = Brush.radialGradient(
      colors = listOf(Color(0x4DFFFFFF), Color.Transparent),
      center = Offset.Zero,
      radius = radius,
    ),
    style = Stroke(width = strokeWidth),
  )

  // Light at bottomRight corner
  drawPath(
    path = shapePath,
    brush = Brush.radialGradient(
      colors = listOf(Color(0x4DFFFFFF), Color.Transparent),
      center = Offset(size.width, size.height),
      radius = radius,
    ),
    style = Stroke(width = strokeWidth),
  )
}
