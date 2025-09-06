// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.many

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.R
import io.github.fletchmckee.liquid.samples.app.theme.FlawedWhite50
import io.github.fletchmckee.liquid.samples.app.theme.LiquidPurple
import io.github.fletchmckee.liquid.samples.app.utils.blendMode
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import io.github.fletchmckee.liquid.samples.app.utils.thenIf

@Composable
fun ManyLiquidNodesScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  useLiquid: Boolean = true,
  initialFrost: Float = 0f,
) = Box(modifier) {
  NyCityBackground(liquidState, useLiquid)
  LiquidNodesList(
    liquidState = liquidState,
    useLiquid = useLiquid,
    initialFrost = initialFrost,
  )
}

@Composable
private fun NyCityBackground(
  liquidState: LiquidState,
  useLiquid: Boolean,
) = Image(
  painter = painterResource(R.drawable.ny_city),
  contentDescription = null,
  contentScale = ContentScale.Crop,
  modifier = Modifier
    .fillMaxSize()
    .thenIf(useLiquid) {
      liquefiable(liquidState)
    },
)

@Composable
private fun LiquidNodesList(
  liquidState: LiquidState,
  useLiquid: Boolean,
  initialFrost: Float,
) = LazyColumn(
  modifier = Modifier
    .fillMaxSize()
    .padding(horizontal = 32.dp)
    .clipToBounds()
    .testTag("liquidNodesList")
    .semantics { testTagsAsResourceId = true },
  contentPadding = WindowInsets.systemBars.asPaddingValues(),
  verticalArrangement = Arrangement.spacedBy(16.dp),
) {
  items(
    count = 500,
    key = { it },
    contentType = { "liquidNodeRow" },
  ) { index ->
    LiquidNodeRow(
      liquidState = liquidState,
      index = index,
      useLiquid = useLiquid,
      initialFrost = initialFrost,
    )
  }
}

@Composable
private fun LiquidNodeRow(
  liquidState: LiquidState,
  index: Int,
  useLiquid: Boolean,
  initialFrost: Float,
  shape: Shape = RoundedCornerShape(15),
  gradientColors: List<Color> = listOf(LiquidPurple, Color.Transparent, FlawedWhite50),
  shaderBrush: ShaderBrush = rememberShaderBrush(gradientColors),
) = Row(
  modifier = Modifier
    .fillMaxWidth()
    .aspectRatio(2f)
    .thenIf(useLiquid) {
      liquid(liquidState) {
        this.frost = initialFrost.dp
        this.edge = 0.05f
        this.shape = shape
      }
    }
    .background(shaderBrush, shape)
    .testTag("liquidNode$index")
    .semantics { testTagsAsResourceId = true },
  verticalAlignment = Alignment.CenterVertically,
) {
  Text(
    text = index.toString(),
    color = Color.White,
    style = MaterialTheme.typography.headlineLarge.merge(
      fontSize = 28.sp,
      fontWeight = FontWeight.Bold,
    ),
    textAlign = TextAlign.Center,
    modifier = Modifier
      .fillMaxWidth()
      .padding(12.dp)
      // Helps improve the text legibility on light surfaces.
      .blendMode(BlendMode.Difference),
  )
}
