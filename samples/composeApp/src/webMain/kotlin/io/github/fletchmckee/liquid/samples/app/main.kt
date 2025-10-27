// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(
  ExperimentalComposeUiApi::class,
  ExperimentalBrowserHistoryApi::class,
  ExperimentalResourceApi::class,
)

package io.github.fletchmckee.liquid.samples.app

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.compose.rememberNavController
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.theme.LiquidTheme
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import liquid_root.samples.composeapp.generated.resources.Res
import liquid_root.samples.composeapp.generated.resources.dotonbori
import liquid_root.samples.composeapp.generated.resources.moon_and_stars
import liquid_root.samples.composeapp.generated.resources.northern_lights
import liquid_root.samples.composeapp.generated.resources.ny_city
import liquid_root.samples.composeapp.generated.resources.prague_clock
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadImageBitmap

fun main() {
  ComposeViewport("ComposeApp") {
    val navController = rememberNavController()
    LiquidDemos(navController = navController)
    val pragueClock by preloadImageBitmap(Res.drawable.prague_clock)
    val moonAndStars by preloadImageBitmap(Res.drawable.moon_and_stars)
    val northernLights by preloadImageBitmap(Res.drawable.northern_lights)
    val nyCity by preloadImageBitmap(Res.drawable.ny_city)
    val dotonbori by preloadImageBitmap(Res.drawable.dotonbori)

    var minTimeElapsed by rememberSaveable { mutableStateOf(false) }
    val resourcesLoaded = pragueClock != null &&
      moonAndStars != null &&
      northernLights != null &&
      nyCity != null &&
      dotonbori != null

    LaunchedEffect(Unit) {
      delay(2.5.seconds)
      minTimeElapsed = true
    }

    when {
      resourcesLoaded && minTimeElapsed -> LiquidDemos(navController = navController)
      else -> WebLoadingScreen()
    }

    LaunchedEffect(Unit) {
      navController.bindToBrowserNavigation()
    }
  }
}

@Composable
private fun WebLoadingScreen(
  liquidState: LiquidState = rememberLiquidState(),
) = LiquidTheme {
  Box(Modifier.fillMaxSize()) {
    Box(
      Modifier
        .fillMaxSize()
        .liquefiable(liquidState)
        .background(rememberShaderBrush()),
    )

    LiquidLoadingBox(
      liquidState = liquidState,
      modifier = Modifier
        .size(250.dp)
        .align(Alignment.Center),
    )
  }
}

@Composable
private fun LiquidLoadingBox(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  loadingShape: Shape = RoundedCornerShape(30),
) {
  val infiniteTransition = rememberInfiniteTransition()
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 720f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Restart,
      initialStartOffset = StartOffset(500),
    ),
    label = "rotate",
  )

  Box(
    modifier
      .graphicsLayer {
        rotationZ = rotation
      }
      .liquid(liquidState) {
        edge = 0.05f
        shape = loadingShape
      },
  )
}
