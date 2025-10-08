// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.stickyheader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.BlueRedGradient
import io.github.fletchmckee.liquid.samples.app.utils.isCI
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import io.github.fletchmckee.liquid.samples.app.utils.toPicsumId

@Composable
fun LiquidStickyHeaderScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
) {
  val useLiquid = LocalUseLiquid.current
  val initialFrost = LocalInitialFrost.current

  var frostRadius by remember { mutableFloatStateOf(initialFrost) }

  Scaffold(
    modifier = modifier,
    containerColor = Color.Transparent,
    contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom),
    topBar = {
      TopAppBar(
        title = {
          Slider(
            value = frostRadius,
            onValueChange = { frostRadius = it },
            valueRange = 0f..50f,
            thumb = {
              Box(
                Modifier
                  .size(ButtonDefaults.IconSize)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary),
              )
            },
            track = { state ->
              SliderDefaults.Track(
                sliderState = state,
                drawStopIndicator = null,
                drawTick = { _, _ -> },
                modifier = Modifier.height(8.dp),
              )
            },
            modifier = Modifier
              .padding(horizontal = 24.dp, vertical = 16.dp)
              .fillMaxWidth(),
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
      )
    },
  ) { padding ->
    // We need a sibling node for displaying the background in order for the liquid nodes to sample from it.
    ShaderBackground(liquidState)
    StickyHeaderList(
      liquidState = liquidState,
      useLiquid = useLiquid,
      initialFrost = frostRadius,
      contentPaddingValues = padding,
    )
  }
}

@Composable
private fun ShaderBackground(
  liquidState: LiquidState,
) = Box(
  Modifier
    .fillMaxSize()
    .liquefiable(liquidState)
    .background(rememberShaderBrush()),
)

@Composable
private fun StickyHeaderList(
  liquidState: LiquidState,
  useLiquid: Boolean,
  initialFrost: Float,
  contentPaddingValues: PaddingValues,
  headerShape: Shape = CircleShape,
  stickyHeaderContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) = LazyColumn(
  modifier = Modifier
    .fillMaxSize()
    .clipToBounds()
    .padding(top = contentPaddingValues.calculateTopPadding())
    .padding(horizontal = 8.dp)
    .testTag("stickyHeaderList")
    .semantics { testTagsAsResourceId = true },
  contentPadding = PaddingValues(bottom = contentPaddingValues.calculateBottomPadding()),
  verticalArrangement = Arrangement.spacedBy(16.dp),
) {
  repeat(5) { header ->
    stickyHeader(
      contentType = { "liquidStickyHeader" },
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .thenIf(useLiquid) {
            liquid(liquidState) {
              frost = initialFrost.dp
              refraction = 0.25f
              curve = 0.5f
              edge = 0.1f
              shape = headerShape
              tint = stickyHeaderContainerColor
            }
          },
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
      ImageItem(liquidState, accumulatedIndex)
    }
  }
}

@Composable
private fun ImageItem(
  liquidState: LiquidState,
  index: Int,
  shape: Shape = RoundedCornerShape(5),
) = AsyncImage(
  model = when {
    isCI -> BlueRedGradient
    else -> "https://picsum.photos/id/${index.toPicsumId()}/300/300"
  },
  contentScale = ContentScale.Crop,
  placeholder = ColorPainter(Color.LightGray),
  error = ColorPainter(Color.Magenta),
  contentDescription = null,
  modifier = Modifier
    .fillMaxWidth()
    .aspectRatio(2f)
    // The extra padding is for screenshot tests. Otherwise it is hard to tell if the effect is doing anything
    // give we have to use ColorImages.
    .padding(horizontal = 16.dp)
    // Be sure to place liquefiable nodes before any clip calls
    .liquefiable(liquidState)
    .clip(shape)
    .testTag("imageItem$index")
    .semantics { testTagsAsResourceId = true },
)
