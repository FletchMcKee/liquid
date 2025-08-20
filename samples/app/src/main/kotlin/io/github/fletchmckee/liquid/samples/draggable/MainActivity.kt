package io.github.fletchmckee.liquid.samples.draggable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import io.github.fletchmckee.liquid.samples.draggable.ui.LiquidGlassScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MaterialTheme {
        LiquidGlassScreen()
      }
    }
  }
}
