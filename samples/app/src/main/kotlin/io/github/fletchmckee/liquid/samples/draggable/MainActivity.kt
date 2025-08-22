// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.draggable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier
import io.github.fletchmckee.liquid.samples.draggable.theme.DraggableTheme
import io.github.fletchmckee.liquid.samples.draggable.ui.LiquidGlassScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val useGlass = intent.getBooleanExtra(USE_GLASS, true)
    val initialFrost = intent.getFloatExtra(INITIAL_FROST, 0f)
    setContent {
      DraggableTheme {
        LiquidGlassScreen(
          useGlass = useGlass,
          initialFrost = initialFrost,
          modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.systemBars),
        )
      }
    }
  }

  private companion object {
    const val PACKAGE_NAME = "io.github.fletchmckee.liquid.samples.draggable"
    const val USE_GLASS = "$PACKAGE_NAME.USE_GLASS"
    const val INITIAL_FROST = "$PACKAGE_NAME.INITIAL_FROST"
  }
}
