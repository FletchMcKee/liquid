// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
  val state = rememberWindowState(
    position = WindowPosition.Aligned(Alignment.Center),
    size = DpSize(800.dp, 500.dp),
  )
  Window(
    title = "Liquid",
    onCloseRequest = ::exitApplication,
    state = state,
    alwaysOnTop = true,
  ) {
    LiquidDemos()
  }
}
