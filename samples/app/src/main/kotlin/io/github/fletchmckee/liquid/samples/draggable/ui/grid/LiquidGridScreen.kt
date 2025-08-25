// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.draggable.ui.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.draggable.R
import io.github.fletchmckee.liquid.samples.draggable.utils.thenIf

@Composable
fun LiquidGridScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  useLiquid: Boolean = true,
  initialFrost: Float = 10f,
) = LiquidScaffold(
  topAppBar = {
    LiquidTopAppBar(
      liquidState = liquidState,
      useLiquid = useLiquid,
      initialFrost = initialFrost,
    )
  },
  modifier = modifier,
) { padding ->
  LiquidGrid(
    contentPadding = padding,
    modifier = Modifier
      .fillMaxSize()
      .testTag("liquidGrid")
      .semantics { testTagsAsResourceId = true }
      .thenIf(useLiquid) {
        liquefiable(liquidState)
      },
  )
}

@Composable
fun LiquidGrid(
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
) = LazyVerticalGrid(
  columns = GridCells.Adaptive(128.dp),
  verticalArrangement = Arrangement.spacedBy(8.dp),
  horizontalArrangement = Arrangement.spacedBy(8.dp),
  contentPadding = contentPadding,
  modifier = modifier,
) {
  items(count = 100) { index ->
    ImageGrid(index)
  }
}

@Composable
private fun ImageGrid(
  index: Int,
) {
  val image = when (index % 3) {
    1 -> R.drawable.moon_and_stars
    2 -> R.drawable.ny_city
    else -> R.drawable.northern_lights
  }

  Image(
    painter = painterResource(image),
    contentScale = ContentScale.Crop,
    contentDescription = null,
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f),
  )
}
