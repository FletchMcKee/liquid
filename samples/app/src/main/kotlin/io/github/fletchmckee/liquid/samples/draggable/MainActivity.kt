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
import io.github.fletchmckee.liquid.samples.draggable.ui.grid.LiquidGridScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val demoType = enumValueOf<DemoType>(intent.getStringExtra(DEMO_TYPE) ?: "Drag")
    val useLiquid = intent.getBooleanExtra(USE_LIQUID, true)
    val initialFrost = intent.getFloatExtra(INITIAL_FROST, 0f)
    setContent {
      DraggableTheme {
        when (demoType) {
          DemoType.Drag -> LiquidGlassScreen(
            useLiquid = useLiquid,
            initialFrost = initialFrost,
            modifier = Modifier
              .fillMaxSize()
              .consumeWindowInsets(WindowInsets.systemBars),
          )
          DemoType.Grid -> LiquidGridScreen(
            useLiquid = useLiquid,
            initialFrost = initialFrost,
            modifier = Modifier
              .fillMaxSize()
              .consumeWindowInsets(WindowInsets.systemBars),
          )
        }
      }
    }
  }

  private companion object {
    const val PACKAGE_NAME = "io.github.fletchmckee.liquid.samples.draggable"
    const val DEMO_TYPE = "$PACKAGE_NAME.DEMO_TYPE"
    const val USE_LIQUID = "$PACKAGE_NAME.USE_LIQUID"
    const val INITIAL_FROST = "$PACKAGE_NAME.INITIAL_FROST"

    enum class DemoType {
      Drag,
      Grid,
    }
  }
}
