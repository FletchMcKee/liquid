// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.os.Build
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import kotlin.sequences.forEach

internal fun ContentDrawScope.recordLiquefiablesIntoLayer(
  layer: GraphicsLayer,
  reusableScope: LiquidScopeImpl,
) {
  val liquefiables = reusableScope.liquefiables
  if (liquefiables.isEmpty()) return

  val bounds = reusableScope.paddedBounds
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
            translate(x, y) { drawLayer(liquefiableLayer) }
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

internal fun Outline.asPath(): Path = when (this) {
  is Outline.Rectangle -> Path().apply { addRect(rect) }
  is Outline.Rounded -> Path().apply { addRoundRect(roundRect) }
  is Outline.Generic -> path
}

// ///////////////
// Backup effects
// ///////////////
internal fun ContentDrawScope.drawBackupLiquidEffect(
  layer: GraphicsLayer,
  reusableScope: LiquidScopeImpl,
) {
  val shapeOutline = reusableScope.shape.createOutline(size, layoutDirection, this)
  val shapePath = shapeOutline.asPath()
  val frostRadius = reusableScope.frostRadius
  if (frostRadius > 0f && Build.VERSION.SDK_INT >= 31) {
    // If we have a valid frostRadius and the device is API 31 or 32, we can at least use Android's BlurEffect.
    recordLiquefiablesIntoLayer(
      layer = layer,
      reusableScope = reusableScope,
    )

    layer.clip = reusableScope.shape != RectangleShape
    layer.renderEffect = reusableScope.renderEffect
      ?: BlurEffect(
        radiusX = frostRadius,
        radiusY = frostRadius,
      ).also { reusableScope.renderEffect = it }

    clipPath(shapePath) {
      translate(-frostRadius, -frostRadius) { drawLayer(layer) }
    }
  }

  // Fill the shape with the tint if one is provided.
  if (reusableScope.tint.alpha > 0f) {
    drawOutline(
      outline = shapeOutline,
      color = reusableScope.tint,
      style = Fill,
    )
  }

  if (reusableScope.edge > 0f) {
    drawBackupEdgeEffect(shapePath)
  }

  drawContent()
}

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
