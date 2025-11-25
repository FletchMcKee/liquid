// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import io.github.fletchmckee.liquid.internal.LiquefiableElement

/**
 * Marks this modifier node as a recording surface whose rendered content can be sampled and
 * displayed through another UI layer using a [LiquidState] effect.
 *
 * This enables the liquid effect by allowing sibling composables to reference and render the
 * content beneath them.
 *
 * **Note:** Make sure to place any draw modifiers (ex. [androidx.compose.ui.draw.shadow] or
 * [androidx.compose.foundation.background]) after this liquefiable node.
 * Otherwise these draw modifiers won't be part of the recording.
 *
 * Example:
 * ```kotlin
 * @Composable
 * private fun ShaderBackground(
 *   liquidState: LiquidState,
 *   shaderBrush: Brush = rememberShaderBrush(),
 * ) = Box(
 *   modifier = Modifier
 *     .fillMaxSize()
 *     .liquefiable(liquidState)
 *     .background(shaderBrush),
 * ) { … }
 * ```
 *
 * @param liquidState The shared [LiquidState] instance that receives this node’s content for sampling.
 */
@Stable
public fun Modifier.liquefiable(
  liquidState: LiquidState,
): Modifier = this then LiquefiableElement(liquidState)
