// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
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
import io.github.fletchmckee.liquid.samples.app.demos.pulltorefresh.PullToRefresh
import io.github.fletchmckee.liquid.samples.app.demos.pulltorefresh.pullToRefreshDestination
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.StickyHeader
import io.github.fletchmckee.liquid.samples.app.demos.stickyheader.stickyHeaderDestination
import io.github.fletchmckee.liquid.samples.app.theme.LiquidShadow
import io.github.fletchmckee.liquid.samples.app.theme.LiquidTheme
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import liquid_root.samples.composeapp.generated.resources.Res
import liquid_root.samples.composeapp.generated.resources.liquid_demos_platform
import org.jetbrains.compose.resources.stringResource

@Composable
fun LiquidDemos(
  modifier: Modifier = Modifier,
  startDestination: Any = Demos,
  useLiquid: Boolean = true,
  initialFrost: Float = 0f,
  initialDispersion: Float = 0f,
  isBenchmark: Boolean = false,
  navController: NavHostController = rememberNavController(),
  pullToRefreshEnabled: Boolean = rememberPullToRefreshEnabled(),
  onNavHostReady: suspend (NavController) -> Unit = {},
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
    if (pullToRefreshEnabled) {
      pullToRefreshDestination(navController)
    }
    clockDestination(navController)
    dragDestination(navController)
    gridDestination(navController)
    stickyHeaderDestination(navController)
    manyDestination(navController)
  }

  val currentOnNavHostReady by rememberUpdatedState(onNavHostReady)
  LaunchedEffect(navController) {
    currentOnNavHostReady(navController)
  }
}

@Composable
internal fun Demos(
  navController: NavController,
  liquidState: LiquidState = rememberLiquidState(),
  pullToRefreshEnabled: Boolean = rememberPullToRefreshEnabled(),
  demosList: List<DemoData> = DemosList.filter { it.navType != PullToRefresh || pullToRefreshEnabled },
) = Scaffold(
  topBar = {
    TopAppBar(
      title = {
        Text(text = stringResource(Res.string.liquid_demos_platform, getPlatform().name))
      },
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
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(padding)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    items(
      items = demosList,
      key = { it.name },
    ) { demo ->
      DemoItem(
        name = demo.name,
        onClick = { navController.navigate(demo.navType) },
        liquidState = liquidState,
      )
    }
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
    .padding(top = 4.dp)
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

@Immutable
internal data class DemoData(
  val name: String,
  val navType: Any,
)

@Stable
private val DemosList = listOf(
  DemoData("Pull to Refresh", PullToRefresh),
  DemoData("Rotating Clock", Clock),
  DemoData("Drag", Drag),
  DemoData("Grid", Grid),
  DemoData("Sticky Header", StickyHeader),
  DemoData("500 Liquid Nodes", Many),
)
