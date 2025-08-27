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
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import io.github.fletchmckee.liquid.samples.draggable.demos.drag.LiquidDraggableScreen
import io.github.fletchmckee.liquid.samples.draggable.demos.grid.LiquidGridScreen
import io.github.fletchmckee.liquid.samples.draggable.theme.LiquidTheme
import okio.Path.Companion.toOkioPath

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val demoType = enumValueOf<DemoType>(intent.getStringExtra(DEMO_TYPE) ?: "Drag")
    val useLiquid = intent.getBooleanExtra(USE_LIQUID, true)
    val initialFrost = intent.getFloatExtra(INITIAL_FROST, 10f)
    setContent {
      // Eventually I will look into a better setup, but I don't want the benchmarks performing a bunch of network requests.
      setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
          .memoryCache {
            MemoryCache.Builder()
              .maxSizePercent(context, 0.25)
              .build()
          }
          .diskCache {
            DiskCache.Builder()
              .directory(
                context.cacheDir
                  .resolve("image_cache")
                  .toOkioPath(),
              )
              .maxSizePercent(0.05)
              .build()
          }
          .crossfade(true)
          .build()
      }

      LiquidTheme {
        when (demoType) {
          DemoType.Drag -> LiquidDraggableScreen(
            useLiquid = useLiquid,
            initialFrost = initialFrost,
            modifier = Modifier
              .fillMaxSize()
              .consumeWindowInsets(WindowInsets.systemBars),
          )
          DemoType.Grid -> LiquidGridScreen(
            useLiquid = useLiquid,
            initialFrost = initialFrost,
            modifier = Modifier.fillMaxSize(),
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

    enum class DemoType { Drag, Grid }
  }
}
