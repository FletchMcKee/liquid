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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.R
import io.github.fletchmckee.liquid.samples.app.demos.drag.LiquidSliders
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun LiquidClockScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  useLiquid: Boolean = true,
  initialFrost: Float = 0f,
) = Box(modifier) {
  var frostRadius by rememberSaveable { mutableFloatStateOf(initialFrost) }
  var curve by rememberSaveable { mutableFloatStateOf(0.25f) }
  var saturation by rememberSaveable { mutableFloatStateOf(1f) }

  PragueClockBackground(liquidState, useLiquid)

  ClockTimer(
    liquidState = liquidState,
    useLiquid = useLiquid,
    modifier = Modifier
      .padding(top = 72.dp)
      .size(250.dp)
      .align(Alignment.TopCenter),
  )

  LiquidRotatingBox(
    liquidState = liquidState,
    useLiquid = useLiquid,
    frostProvider = { frostRadius },
    curveProvider = { curve },
    saturationProvider = { saturation },
    modifier = Modifier
      .zIndex(2f)
      .padding(top = 72.dp)
      .size(250.dp)
      .align(Alignment.TopCenter),
  )

  LiquidSliders(
    liquidState = liquidState,
    useLiquid = useLiquid,
    showSliders = true,
    containerFrost = 20.dp,
    frostProvider = { frostRadius },
    onFrostChange = { frostRadius = it },
    curveProvider = { curve },
    onCurveChange = { curve = it },
    saturationProvider = { saturation },
    onSaturationChange = { saturation = it },
    containerShape = RoundedCornerShape(10),
  )
}

@Composable
private fun PragueClockBackground(
  liquidState: LiquidState,
  useLiquid: Boolean,
) = Image(
  painter = painterResource(R.drawable.prague_clock),
  contentDescription = null,
  contentScale = ContentScale.Crop,
  modifier = Modifier
    .fillMaxSize()
    .thenIf(useLiquid) {
      liquefiable(liquidState)
    },
)

@Composable
private fun ClockTimer(
  liquidState: LiquidState,
  useLiquid: Boolean,
  modifier: Modifier = Modifier,
) {
  val startTime = remember { System.currentTimeMillis() }
  var elapsedMillis by remember { mutableLongStateOf(0L) }

  LaunchedEffect(Unit) {
    while (true) {
      elapsedMillis = System.currentTimeMillis() - startTime
      delay(1.seconds)
    }
  }

  val totalSeconds = elapsedMillis / 1000
  val hours = totalSeconds / 3600
  val minutes = (totalSeconds % 3600) / 60
  val seconds = totalSeconds % 60
  val timeString = "%02d:%02d:%02d".format(hours, minutes, seconds)

  Text(
    text = timeString,
    fontSize = 48.sp,
    textAlign = TextAlign.Center,
    modifier = modifier
      .wrapContentSize(Alignment.Center)
      .thenIf(useLiquid) {
        liquefiable(liquidState)
      }
      .graphicsLayer {
        // Prevents text jitter during continuous transformations.
        compositingStrategy = CompositingStrategy.Offscreen
      }
      .background(Color.White.copy(alpha = 0.75f), CircleShape)
      .padding(16.dp),
  )
}

@Composable
private fun LiquidRotatingBox(
  liquidState: LiquidState,
  useLiquid: Boolean,
  frostProvider: () -> Float,
  curveProvider: () -> Float,
  saturationProvider: () -> Float,
  modifier: Modifier = Modifier,
  boxShape: Shape = RoundedCornerShape(30),
) {
  var dragOffset by remember { mutableStateOf(Offset.Zero) }

  val infiniteTransition = rememberInfiniteTransition()
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 720f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Restart,
    ),
    label = "rotate",
  )

  val scale by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "scale",
  )

  Box(
    modifier
      .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          val x = dragOffset.x + dragAmount.x
          val y = dragOffset.y + dragAmount.y
          dragOffset = Offset(x, y)
        }
      }
      .graphicsLayer {
        rotationZ = rotation
        scaleX = scale
        scaleY = scale
      }
      .shadow(4.dp, boxShape)
      .thenIf(useLiquid) {
        liquid(liquidState) {
          frost = frostProvider().dp
          curve = curveProvider()
          saturation = saturationProvider()
          shape = boxShape
          edge = 0.05f
        }
      },
  )
}
