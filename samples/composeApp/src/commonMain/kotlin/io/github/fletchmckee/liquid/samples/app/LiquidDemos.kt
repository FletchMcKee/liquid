// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.demos.clock.Clock
import io.github.fletchmckee.liquid.samples.app.demos.clock.clockDestination
import io.github.fletchmckee.liquid.samples.app.demos.drag.Drag
import io.github.fletchmckee.liquid.samples.app.demos.drag.dragDestination
import io.github.fletchmckee.liquid.samples.app.demos.grid.Grid
import io.github.fletchmckee.liquid.samples.app.demos.grid.gridDestination
import io.github.fletchmckee.liquid.samples.app.demos.many.Many
import io.github.fletchmckee.liquid.samples.app.demos.many.manyDestination
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.StickyHeader
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.stickyHeaderDestination
import io.github.fletchmckee.liquid.samples.app.theme.LiquidShadow
import io.github.fletchmckee.liquid.samples.app.theme.LiquidTheme
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import kotlinx.serialization.Serializable

@Serializable
data object DemosList

fun NavGraphBuilder.demosListDestination(
  navController: NavController,
) = composable<DemosList> {
  DemosList(navController)
}

@Composable
fun LiquidDemos(
  modifier: Modifier = Modifier,
  startDestination: Any = DemosList,
  useLiquid: Boolean = true,
  initialFrost: Float = 0f,
  initialDispersion: Float = 0f,
  isBenchmark: Boolean = false,
  navController: NavHostController = rememberNavController(),
) = LiquidTheme(
  useLiquid = useLiquid,
  initialFrost = initialFrost,
  initialDispersion = initialDispersion,
  isBenchmark = isBenchmark,
) {
  // Eventually I will look into a better setup, but I don't want the benchmarks performing a bunch of network requests.
  setSingletonImageLoaderFactory { context ->
    context
    ImageLoader.Builder(context)
      .memoryCache {
        MemoryCache.Builder()
          .maxSizePercent(context, 0.25)
          .build()
      }
      .diskCache {
        context.cacheDir()?.let { cacheDir ->
          DiskCache.Builder()
            .directory(cacheDir.resolve("image_cache"))
            .maximumMaxSizeBytes(32 * 1024 * 1024)
            .build()
        }
      }
      .crossfade(true)
      .build()
  }

  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier.fillMaxSize(),
  ) {
    demosListDestination(navController)
    clockDestination(navController)
    dragDestination(navController)
    gridDestination(navController)
    stickyHeaderDestination(navController)
    manyDestination(navController)
  }
}

@Composable
private fun DemosList(
  navController: NavController,
  liquidState: LiquidState = rememberLiquidState(),
) = Scaffold(
  topBar = {
    TopAppBar(
      title = { Text(text = "Liquid Demos (${getPlatform().name})") },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
      ),
    )
  },
) { padding ->
  // Content layer
  Box(
    Modifier
      .fillMaxSize()
      .liquefiable(liquidState)
      .background(rememberShaderBrush()),
  )
  // Control layer
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(padding)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    DemoItem(
      name = "Animating Clock",
      onClick = { navController.navigate(Clock) },
      liquidState = liquidState,
    )

    DemoItem(
      name = "Drag",
      onClick = { navController.navigate(Drag) },
      liquidState = liquidState,
    )

    DemoItem(
      name = "Grid",
      onClick = { navController.navigate(Grid) },
      liquidState = liquidState,
    )

    DemoItem(
      name = "Sticky Header",
      onClick = { navController.navigate(StickyHeader) },
      liquidState = liquidState,
    )

    DemoItem(
      name = "500 Liquid Nodes",
      onClick = { navController.navigate(Many) },
      liquidState = liquidState,
    )
  }
}

@Composable
private fun DemoItem(
  name: String,
  onClick: () -> Unit,
  liquidState: LiquidState,
  cardColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f),
  cardShape: Shape = RoundedCornerShape(15),
) = Row(
  modifier = Modifier
    .fillMaxWidth()
    .dropShadow(cardShape, LiquidShadow)
    .liquid(liquidState) {
      edge = 0.05f
      shape = cardShape
      tint = cardColor
    }
    .padding(horizontal = 8.dp, vertical = 4.dp)
    .clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = null,
      onClick = onClick,
    ),
) {
  Text(
    text = name,
    style = MaterialTheme.typography.titleMedium.copy(
      fontSize = 24.sp,
      fontWeight = FontWeight.Normal,
    ),
    modifier = Modifier.padding(16.dp),
  )
}
