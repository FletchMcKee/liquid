// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.common.PrimarySlider
import io.github.fletchmckee.liquid.samples.app.displayNavIcons
import io.github.fletchmckee.liquid.samples.app.nodes.testTagsAsResourceId
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalIsScreenshotTest
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import kotlin.random.Random
import liquid_root.samples.composeapp.generated.resources.Res
import liquid_root.samples.composeapp.generated.resources.moon_and_stars
import org.jetbrains.compose.resources.painterResource

@Composable
fun LiquidGridScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  navController: NavController = rememberNavController(),
) {
  val useLiquid = LocalUseLiquid.current
  val initialFrost = LocalInitialFrost.current

  var frostRadius by rememberSaveable { mutableFloatStateOf(initialFrost) }
  var cacheKey by rememberSaveable { mutableIntStateOf(Random.nextInt()) }

  LiquidScaffold(
    modifier = modifier,
    topAppBar = {
      LiquidTopAppBar(
        liquidState = liquidState,
        useLiquid = useLiquid,
        frostProvider = { frostRadius },
        navigationIcon = {
          if (displayNavIcons()) {
            IconButton(
              onClick = { navController.popBackStack() },
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back button",
                tint = MaterialTheme.colorScheme.onBackground,
              )
            }
          }
        },
      ) {
        Text(
          text = "Liquid Vertical Grid",
          textAlign = TextAlign.Start,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(start = 24.dp),
        )
      }
    },
    bottomAppBar = {
      LiquidBottomAppBar(
        liquidState = liquidState,
        useLiquid = useLiquid,
        frostProvider = { frostRadius },
      ) {
        PrimarySlider(
          value = frostRadius,
          onValueChange = { frostRadius = it },
          modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        )
      }
    },
  ) { padding ->
    LiquidGrid(
      cacheKey = cacheKey,
      contentPadding = padding,
      modifier = Modifier
        .fillMaxSize()
        .testTag("liquidGrid")
        .testTagsAsResourceId(true)
        .thenIf(useLiquid) {
          liquefiable(liquidState)
        },
    )
  }
}

@Composable
private fun LiquidGrid(
  cacheKey: Int,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  isScreenshotTest: Boolean = LocalIsScreenshotTest.current,
) = LazyVerticalGrid(
  columns = GridCells.Adaptive(120.dp),
  verticalArrangement = Arrangement.spacedBy(8.dp),
  horizontalArrangement = Arrangement.spacedBy(8.dp),
  contentPadding = contentPadding,
  // Need to add a background of some kind, otherwise the gaps between the grids aren't sampled in the liquid effect.
  modifier = modifier
    .background(rememberShaderBrush())
    .padding(horizontal = 8.dp),
) {
  items(count = 100, key = { it }) { index ->
    when {
      isScreenshotTest -> MoonAndStarsBackup(index)
      else -> ImageGrid(index + cacheKey)
    }
  }
}

@Composable
private fun ImageGrid(index: Int) = AsyncImage(
  model = "https://picsum.photos/300?random=$index",
  contentScale = ContentScale.Crop,
  placeholder = ColorPainter(Color.LightGray),
  error = ColorPainter(Color.Magenta),
  contentDescription = null,
  modifier = Modifier
    .fillMaxWidth()
    .aspectRatio(8f / 11f)
    .testTag("imageGrid$index")
    .testTagsAsResourceId(true),
)

@Composable
private fun MoonAndStarsBackup(index: Int) = Image(
  painter = painterResource(Res.drawable.moon_and_stars),
  contentScale = ContentScale.Crop,
  contentDescription = null,
  modifier = Modifier
    .fillMaxWidth()
    .aspectRatio(8f / 11f)
    .testTag("imageGrid$index")
    .testTagsAsResourceId(true),
)
