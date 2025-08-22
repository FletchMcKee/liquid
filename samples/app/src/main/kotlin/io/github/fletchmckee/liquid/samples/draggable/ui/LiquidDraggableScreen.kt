// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.fletchmckee.liquid.samples.draggable.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.draggable.R
import io.github.fletchmckee.liquid.samples.draggable.utils.thenIf

@Composable
fun LiquidGlassScreen(
  modifier: Modifier = Modifier,
  windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
  initialFrost: Float = 0f,
  useGlass: Boolean = true,
  usePager: Boolean = true,
  sliderContainerColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
) {
  val isLandscape = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

  val liquidState = rememberLiquidState()
  var blurRadius by rememberSaveable { mutableFloatStateOf(initialFrost) }
  var refraction by rememberSaveable { mutableFloatStateOf(0.3f) }
  var curve by rememberSaveable { mutableFloatStateOf(0.4f) }
  var sharp by rememberSaveable { mutableFloatStateOf(0.03f) }

  var showSliders by rememberSaveable { mutableStateOf(true) }

  Box(modifier) {
    PagerBackground(
      usePager = usePager,
      modifier = Modifier
        .fillMaxSize()
        .thenIf(useGlass) {
          liquefiable(liquidState)
        },
    )

    SettingsButton(
      onClick = { showSliders = !showSliders },
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(top = 48.dp, start = 32.dp)
        .zIndex(3f)
        .shadow(elevation = 2.dp, shape = CircleShape)
        .thenIf(useGlass) {
          liquid(
            liquidState = liquidState,
            frost = 10.dp,
            shape = CircleShape,
          )
        }
        .background(color = sliderContainerColor, shape = CircleShape),
    )

    LiquidSliders(
      liquidState = liquidState,
      useGlass = useGlass,
      showSliders = showSliders,
      isLandscape = isLandscape,
      frostProvider = { blurRadius },
      onBlurChange = { blurRadius = it },
      refractionProvider = { refraction },
      onLensRefractionChange = { refraction = it },
      curvatureProvider = { curve },
      onCurvatureChange = { curve = it },
      sharpProvider = { sharp },
      onSharpChange = { sharp = it },
      sliderContainerColor = sliderContainerColor,
    )

    LiquidDraggableBox(
      liquidState = liquidState,
      frostProvider = { blurRadius },
      refractionProvider = { refraction },
      curveProvider = { curve },
      sharpProvider = { sharp },
    )
  }
}

@Composable
private fun PagerBackground(
  usePager: Boolean,
  modifier: Modifier = Modifier,
  pagerState: PagerState = rememberPagerState { 3 },
) = when {
  usePager -> HorizontalPager(
    state = pagerState,
    modifier = modifier,
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
  else -> Image(
    painter = painterResource(R.drawable.moon_and_stars),
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = modifier,
  )
}

@Composable
fun SettingsButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(
    modifier = modifier,
    onClick = onClick,
  ) {
    Icon(
      imageVector = Icons.Default.Settings,
      contentDescription = "Sliders visibility button",
      tint = MaterialTheme.colorScheme.onBackground,
    )
  }
}
