// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil3.compose.AsyncImage
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import kotlinx.serialization.Serializable

@Serializable
data object Grid

fun NavGraphBuilder.gridDestination(
  useLiquid: Boolean = true,
  initialFrost: Float = 10f,
) = composable<Grid> {
  LiquidGridScreen(
    useLiquid = useLiquid,
    initialFrost = initialFrost,
    modifier = Modifier.fillMaxSize(),
  )
}

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
    ) {
      Text(
        text = "Liquid Vertical Grid",
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
      )
    }
  },
  bottomAppBar = {
    LiquidBottomAppBar(
      liquidState = liquidState,
      useLiquid = useLiquid,
      initialFrost = initialFrost,
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f),
        )
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f),
        )
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f),
        )
      }
    }
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
  // Need to add a background of some kind, otherwise the gaps between the grids isn't sampled in the liquid effect.
  modifier = modifier
    .background(MaterialTheme.colorScheme.background)
    .padding(horizontal = 8.dp),
) {
  items(count = 100, key = { it + 10 }) { index ->
    ImageGrid(index + 10) // First 10 images are rather boring.
  }
}

@Composable
private fun ImageGrid(
  index: Int,
) {
  // Appears these don't exist with picsum.
  val safeIndex = when (index) {
    86 -> 110
    97 -> 111
    105 -> 112
    else -> index
  }
  AsyncImage(
    model = "https://picsum.photos/id/$safeIndex/200/275",
    contentScale = ContentScale.Crop,
    placeholder = ColorPainter(Color.LightGray),
    error = ColorPainter(Color.Magenta),
    contentDescription = null,
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(8f / 11f),
  )
}
