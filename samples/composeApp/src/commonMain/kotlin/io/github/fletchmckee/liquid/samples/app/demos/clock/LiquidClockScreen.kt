// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.clock

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.common.SliderScaffold
import io.github.fletchmckee.liquid.samples.app.demos.drag.LiquidControls
import io.github.fletchmckee.liquid.samples.app.theme.LiquidShadow
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialDispersion
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.drag
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import liquid_root.samples.composeapp.generated.resources.Res
import liquid_root.samples.composeapp.generated.resources.prague_clock
import org.jetbrains.compose.resources.painterResource

@Composable
fun LiquidClockScreen(
  navController: NavController,
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  disableAnimation: Boolean = false,
) = SliderScaffold(
  navController = navController,
  modifier = modifier,
) {
  Box {
    val initialFrost = LocalInitialFrost.current
    val initialDispersion = LocalInitialDispersion.current

    var frostRadius by rememberSaveable { mutableFloatStateOf(initialFrost) }
    var refraction by rememberSaveable { mutableFloatStateOf(0.25f) }
    var curve by rememberSaveable { mutableFloatStateOf(0.25f) }
    var edge by rememberSaveable { mutableFloatStateOf(0.05f) }
    var saturation by rememberSaveable { mutableFloatStateOf(1f) }
    var dispersion by rememberSaveable { mutableFloatStateOf(initialDispersion) }

    PragueClockBackground(liquidState)

    ClockTimer(
      liquidState = liquidState,
      modifier = Modifier
        .padding(top = 72.dp)
        .size(250.dp)
        .align(Alignment.TopCenter),
    )

    LiquidRotatingBox(
      liquidState = liquidState,
      disableAnimation = disableAnimation,
      frostProvider = { frostRadius },
      refractionProvider = { refraction },
      curveProvider = { curve },
      edgeProvider = { edge },
      saturationProvider = { saturation },
      dispersionProvider = { dispersion },
      modifier = Modifier
        .zIndex(2f)
        .padding(top = 72.dp)
        .size(250.dp)
        .align(Alignment.TopCenter),
    )

    LiquidControls(
      liquidState = liquidState,
      showSliders = true,
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
      dispersionProvider = { dispersion },
      onDispersionChange = { dispersion = it },
      containerShape = RoundedCornerShape(8),
      containerFrost = 30.dp,
      containerRefraction = 0.08f,
      containerEdge = 0.01f,
    )
  }
}

@Composable
private fun PragueClockBackground(
  liquidState: LiquidState,
) = Image(
  painter = painterResource(Res.drawable.prague_clock),
  contentDescription = null,
  contentScale = ContentScale.Crop,
  modifier = Modifier
    .fillMaxSize()
    .thenIf(LocalUseLiquid.current) {
      liquefiable(liquidState)
    },
)

@Composable
private fun ClockTimer(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
) {
  val startTime = remember { Clock.System.now().toEpochMilliseconds() }
  var elapsedMillis by remember { mutableLongStateOf(0L) }

  LaunchedEffect(Unit) {
    while (true) {
      elapsedMillis = Clock.System.now().toEpochMilliseconds() - startTime
      delay(1.seconds)
    }
  }

  val totalSeconds = elapsedMillis / 1000
  val hours = totalSeconds / 3600
  val minutes = (totalSeconds % 3600) / 60
  val seconds = totalSeconds % 60
  val timeString = buildString {
    append(hours.toString().padStart(2, '0'))
    append(":")
    append(minutes.toString().padStart(2, '0'))
    append(":")
    append(seconds.toString().padStart(2, '0'))
  }

  BasicText(
    text = timeString,
    autoSize = TextAutoSize.StepBased(
      minFontSize = 14.sp,
      maxFontSize = 40.sp,
    ),
    style = TextStyle(textAlign = TextAlign.Center),
    maxLines = 1,
    modifier = modifier
      .width(180.dp)
      .wrapContentHeight(Alignment.CenterVertically)
      .liquefiable(liquidState)
      .graphicsLayer {
        // Prevents text jitter during continuous transformations.
        compositingStrategy = CompositingStrategy.Offscreen
      }
      .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(30))
      .padding(16.dp),
  )
}

@Composable
private fun LiquidRotatingBox(
  liquidState: LiquidState,
  disableAnimation: Boolean,
  frostProvider: () -> Float,
  refractionProvider: () -> Float,
  curveProvider: () -> Float,
  edgeProvider: () -> Float,
  saturationProvider: () -> Float,
  dispersionProvider: () -> Float,
  modifier: Modifier = Modifier,
  boxShape: Shape = RoundedCornerShape(30),
) {
  val useLiquid = LocalUseLiquid.current
  var dragOffset by remember { mutableStateOf(Offset.Zero) }

  val infiniteTransition = rememberInfiniteTransition()
  // We're starting at 45 degrees and 1.2 scale for screenshot testing.
  val rotation by infiniteTransition.animateFloat(
    initialValue = if (disableAnimation) 45f else 0f,
    targetValue = if (disableAnimation) 45f else 720f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Restart,
    ),
    label = "rotate",
  )

  val scale by infiniteTransition.animateFloat(
    initialValue = if (disableAnimation) 1.2f else 0.8f,
    targetValue = if (disableAnimation) 1.2f else 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "scale",
  )

  Box(
    modifier
      .drag(
        dragProvider = { dragOffset },
        onDragChange = { dragOffset = it },
      )
      .graphicsLayer {
        rotationZ = rotation
        scaleX = scale
        scaleY = scale
      }
      .dropShadow(boxShape, LiquidShadow)
      .thenIf(useLiquid) {
        liquid(liquidState) {
          frost = frostProvider().dp
          refraction = refractionProvider()
          curve = curveProvider()
          saturation = saturationProvider()
          shape = boxShape
          edge = edgeProvider()
          dispersion = dispersionProvider()
        }
      },
  )
}
