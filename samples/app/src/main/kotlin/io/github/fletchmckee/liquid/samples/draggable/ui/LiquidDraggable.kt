// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.fletchmckee.liquid.samples.draggable.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowWidthSizeClass
import io.github.fletchmckee.liquid.Liquid
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquid
import io.github.fletchmckee.liquid.samples.draggable.R
import io.github.fletchmckee.liquid.samples.draggable.utils.thenIf
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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
  val liquidState = rememberLiquid()

  var screenSize by remember { mutableStateOf(Size.Zero) }
  var blurRadius by rememberSaveable { mutableFloatStateOf(initialFrost) }
  var lensRefraction by rememberSaveable { mutableFloatStateOf(0.3f) }
  var lensCurvature by rememberSaveable { mutableFloatStateOf(0.4f) }
  var sharp by rememberSaveable { mutableFloatStateOf(0.03f) }

  var showSliders by rememberSaveable { mutableStateOf(true) }

  Box(
    modifier
      .fillMaxSize()
      .onSizeChanged { screenSize = it.toSize() },
  ) {
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
        .shadow(2.dp, CircleShape)
        .thenIf(useGlass) {
          liquid(
            liquid = liquidState,
            frost = 10.dp,
            shape = CircleShape,
            tint = sliderContainerColor,
          )
        },
    )

    GlassSliders(
      liquidState = liquidState,
      useGlass = useGlass,
      showSliders = showSliders,
      isLandscape = isLandscape,
      frostProvider = { blurRadius },
      onBlurChange = { blurRadius = it },
      lensRefractionProvider = { lensRefraction },
      onLensRefractionChange = { lensRefraction = it },
      curvatureProvider = { lensCurvature },
      onCurvatureChange = { lensCurvature = it },
      sharpProvider = { sharp },
      onSharpChange = { sharp = it },
      sliderContainerColor = sliderContainerColor,
    )

    DraggableGlassBox(
      liquidState = liquidState,
      screenSize = screenSize,
      frostProvider = { blurRadius },
      lensRefractionProvider = { lensRefraction },
      lensCurvatureProvider = { lensCurvature },
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
private fun DraggableGlassBox(
  liquidState: Liquid,
  screenSize: Size,
  frostProvider: () -> Float,
  lensRefractionProvider: () -> Float,
  lensCurvatureProvider: () -> Float,
  sharpProvider: () -> Float,
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(25),
  useGlass: Boolean = true,
  boxSize: DpSize = DpSize(width = 200.dp, height = 200.dp),
) {
  val density = LocalDensity.current
  var dragOffset by remember(screenSize) {
    mutableStateOf(
      Offset(
        (screenSize.width - with(density) { boxSize.width.toPx() }) / 2,
        (screenSize.height - with(density) { boxSize.height.toPx() }) / 2,
      ),
    )
  }

  Box(
    modifier
      .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
      .size(boxSize)
      .zIndex(2f)
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          val clampedX = max(min(dragOffset.x + dragAmount.x, screenSize.width - size.width), 0f)
          val clampedY = max(min(dragOffset.y + dragAmount.y, screenSize.height - size.height), 0f)
          dragOffset = Offset(clampedX, clampedY)
        }
      }
      .shadow(4.dp, shape)
      .thenIf(useGlass) {
        liquid(
          liquid = liquidState,
          frost = frostProvider().dp,
          shape = shape,
          lensRefraction = lensRefractionProvider(),
          lensCurvature = lensCurvatureProvider(),
          sharp = sharpProvider(),
        )
      },
  ) {
    Text(
      text = "Drag",
      color = MaterialTheme.colorScheme.onBackground,
      style = MaterialTheme.typography.headlineLarge.merge(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
      ),
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter)
        .padding(12.dp),
    )
  }
}

@Composable
private fun BoxScope.GlassSliders(
  liquidState: Liquid,
  useGlass: Boolean,
  showSliders: Boolean,
  isLandscape: Boolean,
  frostProvider: () -> Float,
  onBlurChange: (Float) -> Unit,
  lensRefractionProvider: () -> Float,
  onLensRefractionChange: (Float) -> Unit,
  curvatureProvider: () -> Float,
  onCurvatureChange: (Float) -> Unit,
  sharpProvider: () -> Float,
  onSharpChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(15),
  sliderContainerColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
) {
  AnimatedVisibility(
    visible = showSliders,
    enter = fadeIn(tween(1000)) + expandIn(tween(1000)),
    exit = shrinkOut(tween(1000)) + fadeOut(tween(1000)),
    modifier = modifier
      .align(if (isLandscape) Alignment.CenterEnd else Alignment.BottomCenter)
      .fillMaxWidth(if (isLandscape) 0.4f else 1f)
      .wrapContentHeight()
      .safeContentPadding()
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          // Prevents swiping the HorizontalPager.
          change.consume()
        }
      }
      .thenIf(useGlass) {
        liquefiable(liquidState)
      }
      .shadow(8.dp, shape)
      .thenIf(useGlass) {
        liquid(
          liquid = liquidState,
          frost = 15.dp,
          shape = shape,
          tint = sliderContainerColor,
          lensCurvature = 0.35f,
        )
      },
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      GlassSliderRow(
        text = "Frost:",
        value = frostProvider(),
        onValueChange = onBlurChange,
        steps = 29,
        valueRange = 0f..60f,
        formatter = "%,.0f",
      )

      GlassSliderRow(
        text = "Refraction:",
        value = lensRefractionProvider(),
        onValueChange = onLensRefractionChange,
        steps = 19,
        valueRange = 0f..1f,
      )

      GlassSliderRow(
        text = "Curvature:",
        value = curvatureProvider(),
        onValueChange = onCurvatureChange,
        steps = 19,
        valueRange = 0f..1f,
      )

      GlassSliderRow(
        text = "Sharp:",
        value = sharpProvider(),
        onValueChange = onSharpChange,
        steps = 19,
        valueRange = 0.0f..0.2f,
      )
    }
  }
}

@Composable
private fun ColumnScope.GlassSliderRow(
  text: String,
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  steps: Int = 29,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  formatter: String = "%,.2f",
  enabled: Boolean = true,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp,
      ),
      textAlign = TextAlign.Start,
      modifier = Modifier.weight(1f),
    )

    Text(
      text = formatter.format(value),
      style = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp,
      ),
      textAlign = TextAlign.End,
      modifier = Modifier.weight(1f),
    )
  }

  Slider(
    enabled = enabled,
    value = value,
    onValueChange = onValueChange,
    steps = steps,
    valueRange = valueRange,
    thumb = {
      Box(
        Modifier
          .size(ButtonDefaults.IconSize)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.tertiary),
      )
    },
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
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
