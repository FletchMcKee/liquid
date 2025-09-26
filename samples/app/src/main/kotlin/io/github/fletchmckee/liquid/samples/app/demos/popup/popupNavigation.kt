// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.popup

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Popup

fun NavGraphBuilder.popupDestination(
  useLiquid: Boolean = true,
  initialFrost: Float = 10f,
) = composable<Popup> {
  LiquidPopupScreen(
    useLiquid = useLiquid,
    initialFrost = initialFrost,
    modifier = Modifier
      .fillMaxSize()
      .consumeWindowInsets(WindowInsets.systemBars),
  )
}
