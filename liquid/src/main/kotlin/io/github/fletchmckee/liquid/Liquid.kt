// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.internal.LiquidElement
import io.github.fletchmckee.liquid.internal.liquidBackup

@Stable
public class Liquid {
  private val _liquefiables = mutableStateListOf<Liquefiable>()
  public val liquefiables: List<Liquefiable> get() = _liquefiables.toList()

  internal fun addLiquefiable(liquefiable: Liquefiable) {
    _liquefiables += liquefiable
  }

  internal fun removeLiquefiable(liquefiable: Liquefiable) {
    _liquefiables -= liquefiable
  }
}

@Composable
public fun rememberLiquid(): Liquid = remember { Liquid() }

/**
 * @param liquid Shared state and resources used for sampling background content.
 * @param frost The blur radius applied behind the lens (only effective on supported API levels).
 * @param shape The shape of the effect area, defining the clipping and outline of the effect.
 * @param lensRefraction Controls how much the background distorts through the lens (0 = flat). Defaults to 0.3f.
 * @param lensCurvature Adjusts how strongly the lens curves at its center vs. edges. Defaults to 0.45f.
 * @param sharp Sharpness around the edge highlight.
 * @param tint Optional color overlay applied on top of the lens.
 */
public fun Modifier.liquid(
  liquid: Liquid,
  frost: Dp = 0.dp,
  shape: Shape = RoundedCornerShape(0),
  lensRefraction: Float = 0.3f,
  lensCurvature: Float = 0.45f,
  sharp: Float = 0.05f,
  tint: Color = Color.Transparent,
) = this then when {
  Build.VERSION.SDK_INT >= 33 -> LiquidElement(
    liquid = liquid,
    frost = frost,
    shape = shape,
    lensRefraction = lensRefraction,
    lensCurvature = lensCurvature,
    sharp = sharp,
    tint = tint,
  )
  else ->
    Modifier
      .background(color = tint, shape = shape)
      .liquidBackup(width = 2.dp, cornerPercent = 25)
}
