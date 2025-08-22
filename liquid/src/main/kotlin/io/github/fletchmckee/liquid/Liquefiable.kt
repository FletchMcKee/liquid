// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import android.os.Build
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import io.github.fletchmckee.liquid.internal.LiquefiableElement

@Stable
public class Liquefiable {
  internal var layer: GraphicsLayer? by mutableStateOf(null)

  internal var boundsOnScreen: Rect by mutableStateOf(Rect.Zero)

  override fun toString(): String = """
    Liquefiable(
      layer=$layer,
      boundsOnScreen=$boundsOnScreen,
    )
  """.trimIndent()
}

/**
 * Marks this modifier node as a recording surface whose rendered content can be sampled and displayed through another UI layer using a
 * [LiquidState] effect.
 *
 * This enables visual effects like liquid glass by allowing sibling composables to reference and render the content "beneath"
 * them.
 *
 * On Android 13 (API 33) and above, uses a shader-backed [LiquefiableElement] to capture this node's output.
 * On lower versions, no visual effect is applied and this is a no-op.
 *
 * @param liquidState The shared [LiquidState] instance that receives this node’s content for sampling.
 */
public fun Modifier.liquefiable(
  liquidState: LiquidState,
) = this then when {
  Build.VERSION.SDK_INT >= 33 -> LiquefiableElement(liquidState)
  else -> Modifier
}
