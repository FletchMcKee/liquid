// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.drag

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowWidthSizeClass
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.R
import io.github.fletchmckee.liquid.samples.app.utils.thenIf

@Composable
fun LiquidDraggableScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  useLiquid: Boolean = true,
  initialFrost: Float = 0f,
  windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
  sliderContainerColor: Color = MaterialTheme.colorScheme.surface,
) {
  val isLandscape = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
  // Liquid shader properties
  var frostRadius by rememberSaveable { mutableFloatStateOf(initialFrost) }
  var refraction by rememberSaveable { mutableFloatStateOf(0.3f) }
  var curve by rememberSaveable { mutableFloatStateOf(0.4f) }
  var edge by rememberSaveable { mutableFloatStateOf(0.05f) }
  var cornerPercent by rememberSaveable { mutableIntStateOf(25) }

  var showSliders by rememberSaveable { mutableStateOf(true) }

  Box(modifier) {
    PagerBackground(
      liquidState = liquidState,
      useLiquid = useLiquid,
    )

    SettingsButton(
      liquidState = liquidState,
      onClick = { showSliders = !showSliders },
      useLiquid = useLiquid,
      containerColor = sliderContainerColor,
      modifier = Modifier.align(Alignment.TopStart),
    )

    LiquidSliders(
      liquidState = liquidState,
      useLiquid = useLiquid,
      showSliders = showSliders,
      isLandscape = isLandscape,
      frostProvider = { frostRadius },
      onFrostChange = { frostRadius = it },
      refractionProvider = { refraction },
      onRefractionChange = { refraction = it },
      curveProvider = { curve },
      onCurveChange = { curve = it },
      edgeProvider = { edge },
      onEdgeChange = { edge = it },
      cornerPercent = { cornerPercent },
      onCornerPercentChange = { cornerPercent = it },
      containerColor = sliderContainerColor,
    )

    LiquidDraggableBox(
      liquidState = liquidState,
      frostProvider = { frostRadius },
      refractionProvider = { refraction },
      curveProvider = { curve },
      edgeProvider = { edge },
      cornerPercentProvider = { cornerPercent },
    )
  }
}

@Composable
private fun PagerBackground(
  liquidState: LiquidState,
  useLiquid: Boolean,
  pagerState: PagerState = rememberPagerState { 3 },
) = HorizontalPager(
  state = pagerState,
  modifier = Modifier
    .fillMaxSize()
    .thenIf(useLiquid) {
      liquefiable(liquidState)
    },
) { page ->
  val drawable = when (page) {
    0 -> painterResource(R.drawable.moon_and_stars)
    1 -> painterResource(R.drawable.northern_lights)
    else -> painterResource(R.drawable.ny_city)
  }
  Image(
    painter = drawable,
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier.fillMaxSize(),
  )
}

@Composable
private fun SettingsButton(
  liquidState: LiquidState,
  onClick: () -> Unit,
  useLiquid: Boolean,
  containerColor: Color,
  modifier: Modifier = Modifier,
) = IconButton(
  modifier = modifier
    .padding(top = 48.dp, start = 32.dp)
    .zIndex(3f)
    .shadow(elevation = 2.dp, shape = CircleShape)
    .thenIf(useLiquid) {
      liquid(liquidState) {
        frost = 10.dp
        shape = CircleShape
        tint = containerColor
        edge = 0.05f
      }
    },
  onClick = onClick,
) {
  Icon(
    imageVector = Icons.Default.Settings,
    contentDescription = "Sliders visibility button",
    tint = MaterialTheme.colorScheme.onBackground,
  )
}
