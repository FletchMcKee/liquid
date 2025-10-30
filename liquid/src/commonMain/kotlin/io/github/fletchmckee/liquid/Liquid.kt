// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.fletchmckee.liquid.internal.liquidElement

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
 * Use this to hoist the sampling state and share it between content that should be sampled
 * (ex. background elements) and the consuming [liquid] modifiers that apply the effect.
 *
 * It's recommended (but not required) to share a single [LiquidState] for each screen:
 * ```kotlin
 * @Composable
 * fun SomeScreen(
 *   modifier: Modifier = Modifier,
 *   liquidState: LiquidState = rememberLiquidState(),
 * ) { … }
 * ```
 *
 * @return a stable [LiquidState] that survives recomposition.
 */
@Composable
public fun rememberLiquidState(): LiquidState = remember { LiquidState() }

/**
 * Applies the Liquid effect, sampling pixels recorded in [liquidState].
 *
 * Android 13+ and all other platforms - Full support.
 *
 * Android 12 - The [LiquidScope.refraction], [LiquidScope.curve] and [LiquidScope.dispersion]
 * values are ignored. We will render the [LiquidScope.frost] effect if provided along with a
 * lightweight visual backup that draws a similar [LiquidScope.edge] effect. All other properties
 * are fully supported
 *
 * Android 11 and lower - Same as Android 12 except the [LiquidScope.frost] is also ignored as
 * there is no support for `RenderEffects`.
 *
 * Example:
 * ```kotlin
 * @Composable
 * private fun LiquidRow(
 *   liquidState: LiquidState,
 *   rowShape: Shape = RoundedCornerShape(25),
 * ) = Row(
 *   modifier = Modifier
 *     .liquid(liquidState) {
 *       frost = 10.dp
 *       shape = rowShape
 *     }
 * ) { … }
 * ```
 *
 * **Note:** [block] can be invoked multiple times, which is why it's important for performance to
 * minimize work done inside of it.
 *
 * @param liquidState Shared state that tracks the set of [Liquefiable] sources to sample.
 * @param block A [LiquidScope] block where you define the effect properties.
 */
@Stable
public fun Modifier.liquid(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit = {},
): Modifier = this then liquidElement(liquidState, block)
