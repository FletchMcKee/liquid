// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import io.github.fletchmckee.liquid.internal.LiquidScopeImpl.Companion.Float4Zero

internal fun ContentDrawScope.recordLiquefiablesIntoLayer(
  layer: GraphicsLayer,
  reusableScope: InternalLiquidScope,
) = with(reusableScope) {
  if (positionOnScreen.isUnspecified) return@with
  // Only record content inside the effect's bounds.
  val liquefiables = liquefiables.fastFilter { boundsInRoot.overlaps(it.boundsOnScreen) }
  if (liquefiables.isEmpty()) return@with
  // We avoid unnecessary liquidScope invalidations by observing the mutableState boundsOnScreen
  // and layers here. Changes to these properties will recompose the full draw pass.
  layer.record(intSize) {
    liquefiables.fastForEach { liquefiable ->
      liquefiable.layer
        ?.takeUnless { it.isReleased }
        ?.let { liquefiableLayer ->
          // Position content where it should appear on screen.
          val (x, y) = liquefiable.boundsOnScreen.topLeft - positionOnScreen
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
internal fun Shape.normalizedCornerRadii(
  size: Size,
  density: Density,
  layoutDirection: LayoutDirection = LayoutDirection.Ltr,
): FloatArray = when (this) {
  CircleShape -> {
    floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f)
  }

  is RoundedCornerShape -> {
    when {
      size.minDimension <= 0 -> Float4Zero

      else -> {
        // Similar to the logic in CornerBasedShape, but normalized by the minDimension.
        var topStart = topStart.toPx(size, density)
        var topEnd = topEnd.toPx(size, density)
        var bottomEnd = bottomEnd.toPx(size, density)
        var bottomStart = bottomStart.toPx(size, density)
        val minDimension = size.minDimension

        if (topStart + bottomStart > minDimension) {
          val scale = 1f / (topStart + bottomStart)
          topStart *= scale
          bottomStart *= scale
        } else {
          topStart /= minDimension
          bottomStart /= minDimension
        }

        if (topEnd + bottomEnd > minDimension) {
          val scale = 1f / (topEnd + bottomEnd)
          topEnd *= scale
          bottomEnd *= scale
        } else {
          topEnd /= minDimension
          bottomEnd /= minDimension
        }

        // Users can clip if they want a shape with radii > 50%, but we have to cap the
        // max value at 0.5f to prevent sdf artifacts.
        when (layoutDirection) {
          LayoutDirection.Ltr -> floatArrayOf(
            bottomEnd.fastCoerceAtMost(0.5f),
            topEnd.fastCoerceAtMost(0.5f),
            bottomStart.fastCoerceAtMost(0.5f),
            topStart.fastCoerceAtMost(0.5f),
          )

          LayoutDirection.Rtl -> floatArrayOf(
            bottomStart.fastCoerceAtMost(0.5f),
            topStart.fastCoerceAtMost(0.5f),
            bottomEnd.fastCoerceAtMost(0.5f),
            topEnd.fastCoerceAtMost(0.5f),
          )
        }
      }
    }
  }

  else -> Float4Zero
}

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun Int.has(flag: Int): Boolean = (this and flag) != 0
