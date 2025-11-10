// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.many

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.common.SliderScaffold
import io.github.fletchmckee.liquid.samples.app.nodes.testTagsAsResourceId
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalIsScreenshotTest
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import io.github.fletchmckee.liquid.samples.app.utils.toPicsumId
import liquid_root.samples.composeapp.generated.resources.Res
import liquid_root.samples.composeapp.generated.resources.dotonbori
import liquid_root.samples.composeapp.generated.resources.moon_and_stars
import org.jetbrains.compose.resources.painterResource

@Composable
fun ManyLiquidNodesScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  navController: NavController = rememberNavController(),
) {
  val initialUseLiquid = LocalUseLiquid.current
  var useLiquid by rememberSaveable { mutableStateOf(initialUseLiquid) }
  SliderScaffold(
    navController = navController,
    useLiquidProvider = { useLiquid },
    onUseLiquidChange = { useLiquid = it },
    modifier = modifier,
  ) { paddingValues ->
    DotonboriBackground(liquidState, useLiquid)
    LiquidNodesList(liquidState, useLiquid)
  }
}

@Composable
private fun DotonboriBackground(
  liquidState: LiquidState,
  useLiquid: Boolean,
) = Image(
  painter = painterResource(Res.drawable.dotonbori),
  contentDescription = null,
  contentScale = ContentScale.Crop,
  modifier = Modifier
    .fillMaxSize()
    .thenIf(useLiquid) {
      liquefiable(liquidState)
    }
    .graphicsLayer {
      // This is a hack, but it allows the blur to sample pixels outside of the viewport.
      scaleX = 1.1f
      scaleY = 1.1f
    },
)

@Composable
private fun LiquidNodesList(
  liquidState: LiquidState,
  useLiquid: Boolean,
  initialFrost: Float = LocalInitialFrost.current + 20f,
) = LazyColumn(
  modifier = Modifier
    .fillMaxSize()
    .padding(horizontal = 12.dp)
    .testTag("liquidNodesList")
    .testTagsAsResourceId(true),
  contentPadding = WindowInsets.systemBars.asPaddingValues(),
  verticalArrangement = Arrangement.spacedBy(16.dp),
) {
  items(
    count = 500,
    key = { it },
    contentType = { "liquidNodeRow" },
  ) { index ->
    LiquidCard(
      liquidState = liquidState,
      index = index,
      useLiquid = useLiquid,
      initialFrost = initialFrost,
    )
  }
}

@Composable
private fun LiquidCard(
  liquidState: LiquidState,
  index: Int,
  useLiquid: Boolean,
  initialFrost: Float,
  cardShape: Shape = RoundedCornerShape(5),
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  isScreenshotTest: Boolean = LocalIsScreenshotTest.current,
) = Column(
  modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 4.dp) // For the shadow
    .then(
      when {
        useLiquid ->
          Modifier
            // dropShadow causes some lag with wasm/js.
            .shadow(elevation = 4.dp, shape = cardShape)
            .liquid(liquidState) {
              frost = initialFrost.dp
              refraction = 0.05f
              curve = 0.05f
              edge = 0.01f
              shape = cardShape
              tint = containerColor
            }
        else -> Modifier.background(containerColor, cardShape)
      },
    )
    .padding(24.dp)
    .testTag("liquidNode$index")
    .testTagsAsResourceId(true),
  verticalArrangement = Arrangement.spacedBy(16.dp),
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
  ) {
    when {
      isScreenshotTest -> MoonAndStarsBackup()
      else -> CardImage(index)
    }

    Column(verticalArrangement = Arrangement.SpaceEvenly) {
      Text(
        text = "Card $index",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineLarge.merge(
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
        ),
        modifier = Modifier.padding(start = 16.dp),
      )

      Text(
        text = "Description $index",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineLarge.merge(
          fontSize = 20.sp,
          fontWeight = FontWeight.SemiBold,
        ),
        modifier = Modifier.padding(start = 16.dp),
      )
    }
  }

  Text(
    text = LoremIpsum,
    color = MaterialTheme.colorScheme.onSurface,
    style = MaterialTheme.typography.labelLarge,
  )
}

@Composable
private fun CardImage(
  index: Int,
  shape: Shape = RoundedCornerShape(10),
) = AsyncImage(
  model = "https://picsum.photos/id/${index.toPicsumId()}/300/300",
  contentScale = ContentScale.Crop,
  placeholder = ColorPainter(Color.LightGray),
  error = ColorPainter(Color.Magenta),
  contentDescription = null,
  modifier = Modifier
    .size(80.dp)
    .clip(shape),
)

@Composable
private fun MoonAndStarsBackup(
  shape: Shape = RoundedCornerShape(10),
) = Image(
  painter = painterResource(Res.drawable.moon_and_stars),
  contentScale = ContentScale.Crop,
  contentDescription = null,
  modifier = Modifier
    .size(80.dp)
    .clip(shape),
)

internal val LoremIpsum = """
  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
  Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
  Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
  Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
""".trimIndent()
