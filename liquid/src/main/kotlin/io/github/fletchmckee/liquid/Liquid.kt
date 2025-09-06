// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.fletchmckee.liquid.internal.LiquidBackupElement
import io.github.fletchmckee.liquid.internal.LiquidElement

/**
 * State manager of recorded [Liquefiable] nodes to be rendered into [liquid] effect nodes.
 */
@Stable
public class LiquidState {
  internal val liquefiables = mutableStateListOf<Liquefiable>()
}

/**
 * Remembers a single [LiquidState] instance in the current composition.
 *
 * Use this to hoist the sampling state and share it between content that should be sampled (ex. background elements)
 * and the consuming [liquid] modifiers that apply the effect.
 *
 * @return a stable [LiquidState] that survives recomposition.
 */
@Composable
public fun rememberLiquidState(): LiquidState = remember { LiquidState() }

/**
 * Applies the Liquid effect, sampling pixels recorded in [liquidState].
 *
 * API 33+ - Has full support, the liquid effect is only rendered for these devices as it uses
 * [android.graphics.RuntimeShader] for the effect.
 * API 31-32 - The [LiquidScope.refraction] and [LiquidScope.curve] values are ignored, but will render
 * the frost effect if provided along with a lightweight visual backup that draws a similar edge effect.
 * API 30 and lower - The [LiquidScope.frost] is also ignored as there is no support for the RenderEffect.
 * Will draw a lightweight backup to the edge effect along with the other provided values.
 *
 * NOTE: [block] can be invoked multiple times, which is why it's important for performance to minimize
 * work done inside of it.
 *
 * @param liquidState Shared state that tracks the set of [Liquefiable] sources to sample.
 * @param block A [LiquidScope] block where you define the effect properties.
 */
public fun Modifier.liquid(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit = {},
): Modifier = this then when {
  Build.VERSION.SDK_INT >= 33 -> LiquidElement(
    liquidState = liquidState,
    block = block,
  )
  else -> LiquidBackupElement(
    liquidState = liquidState,
    block = block,
  )
}
