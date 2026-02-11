// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.clock

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.common.LiquidControls
import io.github.fletchmckee.liquid.samples.app.common.LiquidScopeManager
import io.github.fletchmckee.liquid.samples.app.common.PicsumBackground
import io.github.fletchmckee.liquid.samples.app.common.SliderScaffold
import io.github.fletchmckee.liquid.samples.app.common.retainLiquidScopeManager
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialDispersion
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalIsScreenshotTest
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.drag
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import liquid_root.samples.shared.generated.resources.Res
import liquid_root.samples.shared.generated.resources.clock_format
import liquid_root.samples.shared.generated.resources.prague_clock
import org.jetbrains.compose.resources.stringResource

@Composable
fun LiquidClockScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  navController: NavController = rememberNavController(),
  windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
) {
  val initialUseLiquid = LocalUseLiquid.current
  val initialFrost = LocalInitialFrost.current
  val initialDispersion = LocalInitialDispersion.current
  val isLandscape = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

  val liquidScopeManager = retainLiquidScopeManager(
    initialFrost = initialFrost,
    initialDispersion = initialDispersion,
    initialEdge = 0.1f,
    initialCornerPercent = 30,
  )

  var useLiquid by rememberSaveable { mutableStateOf(initialUseLiquid) }

  SliderScaffold(
    navController = navController,
    useLiquidProvider = { useLiquid },
    onUseLiquidChange = { useLiquid = it },
    modifier = modifier,
  ) {
    Box {
      var cacheKey by rememberSaveable { mutableIntStateOf(Random.nextInt(until = 2000)) }
      PicsumBackground(
        liquidState = liquidState,
        cacheKey = cacheKey,
        useLiquid = useLiquid,
        defaultDrawable = Res.drawable.prague_clock,
      )

      ClockTimer(
        liquidState = liquidState,
        useLiquid = useLiquid,
        modifier = Modifier
          .systemBarsPadding()
          .padding(top = 72.dp)
          .size(width = 200.dp, height = 250.dp)
          .align(Alignment.TopCenter),
      )

      LiquidRotatingBox(
        liquidState = liquidState,
        useLiquid = useLiquid,
        liquidScopeManager = liquidScopeManager,
        modifier = Modifier
          .zIndex(2f)
          .systemBarsPadding()
          .padding(top = 72.dp)
          .size(250.dp)
          .align(Alignment.TopCenter),
      )

      LiquidControls(
        liquidState = liquidState,
        showSliders = true,
        liquidScopeManager = liquidScopeManager,
        containerShape = RoundedCornerShape(8),
        containerFrost = 30.dp,
        containerRefraction = 0.08f,
        containerEdge = 0.01f,
        useLiquid = useLiquid,
        isLandscape = isLandscape,
      )
    }
  }
}

@Composable
private fun ClockTimer(
  liquidState: LiquidState,
  useLiquid: Boolean,
  modifier: Modifier = Modifier,
) {
  val startTime = remember { Clock.System.now().toEpochMilliseconds() }
  var elapsedMillis by remember { mutableLongStateOf(0L) }

  LaunchedEffect(Unit) {
    while (isActive) {
      elapsedMillis = Clock.System.now().toEpochMilliseconds() - startTime
      delay(1.seconds)
    }
  }

  val totalSeconds = elapsedMillis / 1000
  val hours = totalSeconds / 3600
  val minutes = (totalSeconds % 3600) / 60
  val seconds = totalSeconds % 60
  val timeString = stringResource(
    Res.string.clock_format,
    hours.toString().padStart(2, '0'),
    minutes.toString().padStart(2, '0'),
    seconds.toString().padStart(2, '0'),
  )

  BasicText(
    text = timeString,
    autoSize = TextAutoSize.StepBased(
      minFontSize = 14.sp,
      maxFontSize = 40.sp,
    ),
    style = TextStyle(textAlign = TextAlign.Center),
    maxLines = 1,
    modifier = modifier
      .wrapContentHeight(Alignment.CenterVertically)
      .thenIf(useLiquid) {
        liquefiable(liquidState)
      }
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
  useLiquid: Boolean,
  liquidScopeManager: LiquidScopeManager,
  modifier: Modifier = Modifier,
  isScreenshotTest: Boolean = LocalIsScreenshotTest.current,
  fallbackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
  var dragOffset by remember { mutableStateOf(Offset.Zero) }

  val infiniteTransition = rememberInfiniteTransition()
  // Animations are disabled for screenshot tests.
  val rotation by infiniteTransition.animateFloat(
    initialValue = if (isScreenshotTest) 45f else 0f,
    targetValue = 720f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Restart,
      initialStartOffset = StartOffset(1000),
    ),
    label = "rotate",
  )

  val scale by infiniteTransition.animateFloat(
    initialValue = if (isScreenshotTest) 1.2f else 0.8f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Reverse,
      initialStartOffset = StartOffset(1000),
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
      .then(
        when {
          useLiquid -> Modifier.liquid(liquidState) {
            frost = liquidScopeManager.frost.dp
            refraction = liquidScopeManager.refraction
            curve = liquidScopeManager.curve
            saturation = liquidScopeManager.saturation
            shape = RoundedCornerShape(liquidScopeManager.cornerPercent)
            edge = liquidScopeManager.edge
            dispersion = liquidScopeManager.dispersion
            contrast = liquidScopeManager.contrast
          }

          else -> Modifier.background(fallbackColor, RoundedCornerShape(liquidScopeManager.cornerPercent))
        },
      ),
  )
}
