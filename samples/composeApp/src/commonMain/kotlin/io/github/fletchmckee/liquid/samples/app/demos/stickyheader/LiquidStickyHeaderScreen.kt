// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.stickyheader

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.common.ShaderBackground
import io.github.fletchmckee.liquid.samples.app.common.SliderScaffold
import io.github.fletchmckee.liquid.samples.app.demos.many.LoremIpsum
import io.github.fletchmckee.liquid.samples.app.nodes.testTagsAsResourceId
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalIsScreenshotTest
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import kotlin.random.Random
import liquid_root.samples.composeapp.generated.resources.Res
import liquid_root.samples.composeapp.generated.resources.moon_and_stars
import org.jetbrains.compose.resources.painterResource

@Composable
fun LiquidStickyHeaderScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  navController: NavController = rememberNavController(),
  listState: LazyListState = rememberLazyListState(),
) {
  val initialUseLiquid = LocalUseLiquid.current
  val initialFrost = LocalInitialFrost.current

  var frostRadius by rememberSaveable { mutableFloatStateOf(initialFrost) }
  var useLiquid by rememberSaveable { mutableStateOf(initialUseLiquid) }
  var cacheKey by rememberSaveable { mutableIntStateOf(Random.nextInt()) }

  SliderScaffold(
    navController = navController,
    frostProvider = { frostRadius },
    onFrostChange = { frostRadius = it },
    useLiquidProvider = { useLiquid },
    onUseLiquidChange = { useLiquid = it },
    modifier = modifier,
  ) { padding ->
    // We need a sibling node for displaying the background in order for the liquid nodes to sample from it.
    ShaderBackground(
      liquidState = liquidState,
      useLiquid = useLiquid,
    )

    StickyHeaderList(
      cacheKey = cacheKey,
      liquidState = liquidState,
      listState = listState,
      useLiquid = useLiquid,
      initialFrost = frostRadius,
      contentPaddingValues = padding,
    )
  }
}

@Composable
private fun StickyHeaderList(
  cacheKey: Int,
  liquidState: LiquidState,
  listState: LazyListState,
  useLiquid: Boolean,
  initialFrost: Float,
  contentPaddingValues: PaddingValues,
  headerShape: Shape = CircleShape,
  stickyHeaderContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) = LazyColumn(
  state = listState,
  modifier = Modifier
    .fillMaxSize()
    .clipToBounds()
    .padding(top = contentPaddingValues.calculateTopPadding())
    .padding(horizontal = 8.dp)
    .testTag("stickyHeaderList")
    .testTagsAsResourceId(true),
  contentPadding = PaddingValues(bottom = contentPaddingValues.calculateBottomPadding()),
  verticalArrangement = Arrangement.spacedBy(16.dp),
) {
  repeat(5) { header ->
    stickyHeader(
      key = "header$header",
      contentType = { "liquidStickyHeader" },
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .then(
            when {
              useLiquid ->
                Modifier
                  .shadow(8.dp, headerShape)
                  .liquid(liquidState) {
                    frost = initialFrost.dp
                    refraction = 0.25f
                    curve = 0.5f
                    edge = 0.1f
                    shape = headerShape
                    tint = stickyHeaderContainerColor
                  }
              else -> Modifier.background(stickyHeaderContainerColor, headerShape)
            },
          ),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = header.toString(),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        )
      }
    }

    items(count = 20, key = { 20 * header + it }, contentType = { "imageItem" }) { index ->
      val accumulatedIndex = 20 * header + index
      CardItem(
        liquidState = liquidState,
        cacheKey = cacheKey,
        index = accumulatedIndex,
        useLiquid = useLiquid,
      )
    }
  }
}

@Composable
private fun CardItem(
  cacheKey: Int,
  liquidState: LiquidState,
  index: Int,
  useLiquid: Boolean,
  shape: Shape = RoundedCornerShape(5),
  containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
  isScreenshotTest: Boolean = LocalIsScreenshotTest.current,
) = Column(
  modifier = Modifier
    .fillMaxWidth()
    .thenIf(useLiquid) {
      // Be sure to place liquefiable nodes before any clip calls
      liquefiable(liquidState)
    }
    .clip(shape)
    .background(containerColor),
  horizontalAlignment = Alignment.CenterHorizontally,
) {
  when {
    isScreenshotTest -> MoonAndStarsBackup(index)
    else -> ImageItem(index + cacheKey)
  }

  Text(
    text = "Card $index",
    color = MaterialTheme.colorScheme.onBackground,
    style = MaterialTheme.typography.labelLarge,
    modifier = Modifier.padding(16.dp),
  )
  Text(
    text = LoremIpsum,
    color = MaterialTheme.colorScheme.onBackground,
    style = MaterialTheme.typography.bodyMedium,
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(bottom = 16.dp),
  )
}

@Composable
private fun ImageItem(
  index: Int,
) = AsyncImage(
  model = "https://picsum.photos/600?random=$index",
  contentScale = ContentScale.Crop,
  placeholder = ColorPainter(Color.LightGray),
  error = ColorPainter(Color.Magenta),
  contentDescription = null,
  modifier = Modifier
    .widthIn(max = 600.dp)
    .aspectRatio(1f)
    .testTag("imageItem$index")
    .testTagsAsResourceId(true),
)

@Composable
private fun MoonAndStarsBackup(index: Int) = Image(
  painter = painterResource(Res.drawable.moon_and_stars),
  contentScale = ContentScale.Crop,
  contentDescription = null,
  modifier = Modifier
    .widthIn(max = 600.dp)
    .aspectRatio(1f)
    .testTag("imageItem$index")
    .testTagsAsResourceId(true),
)
