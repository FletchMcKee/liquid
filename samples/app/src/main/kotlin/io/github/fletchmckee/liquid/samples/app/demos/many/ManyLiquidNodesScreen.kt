// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.many

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.R
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.BlueRedGradient
import io.github.fletchmckee.liquid.samples.app.utils.isCI
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import io.github.fletchmckee.liquid.samples.app.utils.toPicsumId

@Composable
fun ManyLiquidNodesScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
) = Box(modifier) {
  DotonboriBackground(liquidState)
  LiquidNodesList(liquidState)
}

@Composable
private fun DotonboriBackground(
  liquidState: LiquidState,
) = Image(
  painter = painterResource(R.drawable.dotonbori),
  contentDescription = null,
  contentScale = ContentScale.Crop,
  modifier = Modifier
    .fillMaxSize()
    .thenIf(LocalUseLiquid.current) {
      liquefiable(liquidState)
    },
)

@Composable
private fun LiquidNodesList(
  liquidState: LiquidState,
) = LazyColumn(
  modifier = Modifier
    .fillMaxSize()
    .padding(horizontal = 24.dp)
    .clipToBounds()
    .testTag("liquidNodesList")
    .semantics { testTagsAsResourceId = true },
  contentPadding = WindowInsets.systemBars.asPaddingValues(),
  verticalArrangement = Arrangement.spacedBy(24.dp),
) {
  items(
    count = 500,
    key = { it },
    contentType = { "liquidNodeRow" },
  ) { index ->
    LiquidCard(
      liquidState = liquidState,
      index = index,
      useLiquid = LocalUseLiquid.current,
      initialFrost = LocalInitialFrost.current,
    )
  }
}

@Composable
private fun LiquidCard(
  liquidState: LiquidState,
  index: Int,
  useLiquid: Boolean,
  initialFrost: Float,
  cardShape: Shape = RoundedCornerShape(10),
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) = Column(
  modifier = Modifier
    .fillMaxWidth()
    .thenIf(useLiquid) {
      liquid(liquidState) {
        frost = initialFrost.dp
        refraction = 0.1f
        edge = 0.1f
        shape = cardShape
        tint = containerColor
      }
    }
    .padding(24.dp)
    .testTag("liquidNode$index")
    .semantics { testTagsAsResourceId = true },
  verticalArrangement = Arrangement.spacedBy(16.dp),
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CardImage(index)

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
  model = when {
    isCI -> BlueRedGradient
    else -> "https://picsum.photos/id/${index.toPicsumId()}/300/300"
  },
  contentScale = ContentScale.Crop,
  placeholder = ColorPainter(Color.LightGray),
  error = ColorPainter(Color.Magenta),
  contentDescription = null,
  modifier = Modifier
    .size(80.dp)
    .clip(shape),
)

private val LoremIpsum = """
  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
  Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
  Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
  Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
""".trimIndent()
