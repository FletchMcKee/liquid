// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import io.github.fletchmckee.liquid.samples.app.demos.drag.Drag
import io.github.fletchmckee.liquid.samples.app.demos.drag.dragDestination
import io.github.fletchmckee.liquid.samples.app.demos.grid.Grid
import io.github.fletchmckee.liquid.samples.app.demos.grid.gridDestination
import io.github.fletchmckee.liquid.samples.app.demos.many.Many
import io.github.fletchmckee.liquid.samples.app.demos.many.manyNavigation
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.StickyHeader
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.stickyHeaderDestination
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import kotlinx.serialization.Serializable
import okio.Path.Companion.toOkioPath

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
  navController: NavHostController = rememberNavController(),
  useLiquid: Boolean = true,
  initialFrost: Float = 10f,
) {
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

  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier.fillMaxSize(),
  ) {
    demosListDestination(navController)

    dragDestination(
      useLiquid = useLiquid,
      initialFrost = initialFrost,
    )

    gridDestination(
      useLiquid = useLiquid,
      initialFrost = initialFrost,
    )

    stickyHeaderDestination(
      useLiquid = useLiquid,
      initialFrost = initialFrost,
    )

    manyNavigation(
      useLiquid = useLiquid,
      initialFrost = initialFrost,
    )
  }
}

@Composable
private fun DemosList(
  navController: NavController,
) = Scaffold(
  topBar = {
    TopAppBar(
      title = { Text(text = "Liquid Demos") },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
      ),
    )
  },
) { padding ->
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(rememberShaderBrush())
      .padding(padding)
      .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    DemoItem(
      name = "Drag",
      onClick = { navController.navigate(Drag) },
    )

    DemoItem(
      name = "Grid",
      onClick = { navController.navigate(Grid) },
    )

    DemoItem(
      name = "Sticky Header",
      onClick = { navController.navigate(StickyHeader) },
    )

    DemoItem(
      name = "500 Liquid Nodes",
      onClick = { navController.navigate(Many) },
    )
  }
}

@Composable
private fun DemoItem(
  name: String,
  onClick: () -> Unit,
) = Row(
  modifier = Modifier
    .fillMaxWidth()
    .clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = null,
      onClick = onClick,
    ),
) {
  Text(
    text = name,
    style = MaterialTheme.typography.titleMedium.copy(
      fontSize = 20.sp,
      fontWeight = FontWeight.Normal,
    ),
  )
}
