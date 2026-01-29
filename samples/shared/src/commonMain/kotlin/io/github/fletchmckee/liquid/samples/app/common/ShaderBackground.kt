// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import io.github.fletchmckee.liquid.samples.app.utils.thenIf

/**
 * Simple linear gradient shader, blending the background colorScheme with the primary color.
 */
@Composable
fun ShaderBackground(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  useLiquid: Boolean = LocalUseLiquid.current,
) = Box(
  modifier
    .fillMaxSize()
    .thenIf(useLiquid) {
      liquefiable(liquidState)
    }
    .background(rememberShaderBrush()),
)
