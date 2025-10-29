// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.drag

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.common.LiquidControls
import io.github.fletchmckee.liquid.samples.app.displayNavIcons
import io.github.fletchmckee.liquid.samples.app.nodes.testTagsAsResourceId
import io.github.fletchmckee.liquid.samples.app.theme.LiquidShadow
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialDispersion
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import liquid_root.samples.composeapp.generated.resources.Res
import liquid_root.samples.composeapp.generated.resources.moon_and_stars
import liquid_root.samples.composeapp.generated.resources.northern_lights
import liquid_root.samples.composeapp.generated.resources.ny_city
import org.jetbrains.compose.resources.painterResource

@Composable
fun LiquidDraggableScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  navController: NavController = rememberNavController(),
  sliderContainerColor: Color = MaterialTheme.colorScheme.surface,
) {
  val initialFrost = LocalInitialFrost.current
  val initialDispersion = LocalInitialDispersion.current
  // Liquid shader properties
  var frostRadius by rememberSaveable { mutableFloatStateOf(initialFrost) }
  var refraction by rememberSaveable { mutableFloatStateOf(0.25f) }
  var curve by rememberSaveable { mutableFloatStateOf(0.25f) }
  var edge by rememberSaveable { mutableFloatStateOf(0.05f) }
  var saturation by rememberSaveable { mutableFloatStateOf(1f) }
  var cornerPercent by rememberSaveable { mutableIntStateOf(25) }
  var dispersion by rememberSaveable { mutableFloatStateOf(initialDispersion) }

  var showSliders by rememberSaveable { mutableStateOf(true) }

  Box(modifier) {
    // Content layer
    PagerBackground(liquidState)

    // Controls layer
    BackButton(
      liquidState = liquidState,
      onClick = { navController.popBackStack() },
      containerColor = sliderContainerColor,
      modifier = Modifier.align(Alignment.TopStart),
    )

    SettingsColumn(
      liquidState = liquidState,
      onControlsClick = { showSliders = !showSliders },
      containerColor = sliderContainerColor,
      modifier = Modifier.align(Alignment.TopEnd),
    )

    LiquidControls(
      liquidState = liquidState,
      showSliders = showSliders,
      frostProvider = { frostRadius },
      onFrostChange = { frostRadius = it },
      refractionProvider = { refraction },
      onRefractionChange = { refraction = it },
      curveProvider = { curve },
      onCurveChange = { curve = it },
      edgeProvider = { edge },
      onEdgeChange = { edge = it },
      saturationProvider = { saturation },
      onSaturationChange = { saturation = it },
      cornerPercentProvider = { cornerPercent },
      onCornerPercentChange = { cornerPercent = it },
      dispersionProvider = { dispersion },
      onDispersionChange = { dispersion = it },
      containerColor = sliderContainerColor,
    )

    LiquidDraggableBox(
      liquidState = liquidState,
      frostProvider = { frostRadius },
      refractionProvider = { refraction },
      curveProvider = { curve },
      edgeProvider = { edge },
      saturationProvider = { saturation },
      shapeProvider = { RoundedCornerShape(cornerPercent) },
      dispersionProvider = { dispersion },
    )
  }
}

@Composable
private fun PagerBackground(
  liquidState: LiquidState,
  pagerState: PagerState = rememberPagerState { 3 },
) = HorizontalPager(
  state = pagerState,
  modifier = Modifier
    .fillMaxSize()
    .thenIf(LocalUseLiquid.current) {
      liquefiable(liquidState)
    },
) { page ->
  val drawable = when (page) {
    0 -> painterResource(Res.drawable.moon_and_stars)
    1 -> painterResource(Res.drawable.northern_lights)
    else -> painterResource(Res.drawable.ny_city)
  }
  Image(
    painter = drawable,
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier.fillMaxSize(),
  )
}

@Composable
private fun SettingsColumn(
  liquidState: LiquidState,
  onControlsClick: () -> Unit,
  containerColor: Color,
  modifier: Modifier = Modifier,
) = Column(
  modifier = modifier
    .systemBarsPadding()
    .padding(top = 24.dp, end = 24.dp)
    .zIndex(3f),
  verticalArrangement = Arrangement.spacedBy(16.dp),
) {
  ControlsButton(
    liquidState = liquidState,
    onClick = onControlsClick,
    containerColor = containerColor,
  )
}

@Composable
private fun BackButton(
  liquidState: LiquidState,
  onClick: () -> Unit,
  containerColor: Color,
  modifier: Modifier = Modifier,
) {
  if (displayNavIcons()) {
    IconButton(
      modifier = modifier
        .systemBarsPadding()
        .padding(top = 24.dp, start = 24.dp)
        .zIndex(3f)
        .dropShadow(CircleShape, LiquidShadow)
        .thenIf(LocalUseLiquid.current) {
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
        imageVector = Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = "Back button",
        tint = MaterialTheme.colorScheme.onBackground,
      )
    }
  }
}

@Composable
private fun ControlsButton(
  liquidState: LiquidState,
  onClick: () -> Unit,
  containerColor: Color,
) = IconButton(
  modifier = Modifier
    .dropShadow(CircleShape, LiquidShadow)
    .thenIf(LocalUseLiquid.current) {
      liquid(liquidState) {
        frost = 10.dp
        shape = CircleShape
        tint = containerColor
        edge = 0.05f
      }
    }
    .testTag("settingsButton")
    .testTagsAsResourceId(true),
  onClick = onClick,
) {
  Icon(
    imageVector = Icons.Default.Menu,
    contentDescription = "Sliders visibility button",
    tint = MaterialTheme.colorScheme.onBackground,
  )
}
