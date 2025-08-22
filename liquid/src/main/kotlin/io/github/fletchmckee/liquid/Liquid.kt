// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import android.os.Build
import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.internal.LiquidElement
import io.github.fletchmckee.liquid.internal.liquidBackup

/**
 * State manager of recorded [Liquefiable] nodes to be rendered into [liquid] effect nodes.
 */
@Stable
public class LiquidState {
  internal val liquefiables = mutableStateListOf<Liquefiable>()

  internal fun addLiquefiable(liquefiable: Liquefiable) {
    liquefiables += liquefiable
  }

  internal fun removeLiquefiable(liquefiable: Liquefiable) {
    liquefiables -= liquefiable
  }
}

@Composable
public fun rememberLiquidState(): LiquidState = remember { LiquidState() }

/**
 * @param liquidState Shared state and resources used for sampling background content.
 * @param frost The blur radius applied behind the liquid effect, giving the appearance of frost. Negative values are ignored.
 * Defaults to 0.dp.
 * @param shape The shape of the effect area, defining the clipping and outline of the effect. Defaults to [RectangleShape].
 * @param refraction Controls how much the background distorts through the lens. Setting this to 0 removes the liquid effect altogether,
 * nullifying any [curve] value. Defaults to 0.3f.
 * @param curve Adjusts how strongly the lens curves at its center vs. edges. Setting this to 0 removes the liquid effect altogether,
 * nullifying any [refraction] value. Defaults to 0.45f.
 * @param sharp Sharpness around the edge highlight.
 */
public fun Modifier.liquid(
  liquidState: LiquidState,
  frost: Dp = 0.dp,
  shape: Shape = RectangleShape,
  @FloatRange(from = 0.0, to = 1.0) refraction: Float = 0.3f,
  @FloatRange(from = 0.0, to = 1.0) curve: Float = 0.45f,
  sharp: Float = 0.05f,
) = this then when {
  Build.VERSION.SDK_INT >= 33 -> LiquidElement(
    liquidState = liquidState,
    frost = frost,
    shape = shape,
    refraction = refraction,
    curve = curve,
    sharp = sharp,
  )
  else -> Modifier.liquidBackup(width = 2.dp, shape = shape)
}
