// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.drag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.samples.app.utils.safeShadow
import io.github.fletchmckee.liquid.samples.app.utils.thenIf

@Composable
fun BoxScope.LiquidSliders(
  liquidState: LiquidState,
  useLiquid: Boolean,
  showSliders: Boolean,
  isLandscape: Boolean,
  frostProvider: () -> Float,
  onFrostChange: (Float) -> Unit,
  refractionProvider: () -> Float,
  onRefractionChange: (Float) -> Unit,
  curveProvider: () -> Float,
  onCurveChange: (Float) -> Unit,
  edgeProvider: () -> Float,
  onEdgeChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(15),
  containerColor: Color = MaterialTheme.colorScheme.surface,
) = AnimatedVisibility(
  visible = showSliders,
  enter = fadeIn(tween(1000)) + expandIn(tween(1000)),
  exit = fadeOut(tween(1000)) + shrinkOut(tween(1000)),
  modifier = modifier
    .align(if (isLandscape) Alignment.CenterEnd else Alignment.BottomCenter)
    .fillMaxWidth(if (isLandscape) 0.4f else 1f)
    .wrapContentHeight()
    .padding(WindowInsets.systemBars.asPaddingValues())
    .padding(16.dp)
    .pointerInput(Unit) {
      detectDragGestures { change, dragAmount ->
        // Prevents swiping the HorizontalPager.
        change.consume()
      }
    }
    .thenIf(useLiquid) {
      liquefiable(liquidState)
    }
    .safeShadow(elevation = 8.dp, shape = shape)
    .thenIf(useLiquid) {
      liquid(liquidState) {
        this.frost = 15.dp
        this.shape = shape
        this.curve = 0.35f
        this.refraction = 0.3f
        this.edge = 0.05f
        this.tint = containerColor
      }
    },
) {
  Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    LiquidSliderRow(
      text = "Frost",
      value = frostProvider(),
      onValueChange = onFrostChange,
      steps = 24,
      valueRange = 0f..50f,
      formatter = "%,.0f",
      sliderTestTag = "frostSlider",
      thumbTestTag = "frostThumb",
    )

    LiquidSliderRow(
      text = "Refraction",
      value = refractionProvider(),
      onValueChange = onRefractionChange,
      valueRange = 0f..1f,
    )

    LiquidSliderRow(
      text = "Curve",
      value = curveProvider(),
      onValueChange = onCurveChange,
      valueRange = 0f..1f,
    )

    LiquidSliderRow(
      text = "Edge",
      value = edgeProvider(),
      onValueChange = onEdgeChange,
      valueRange = 0.0f..0.2f,
    )
  }
}

@Composable
fun LiquidSliderRow(
  text: String,
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  steps: Int = 19,
  valueRange: FloatRange = 0f..1f,
  formatter: String = "%,.2f",
  sliderTestTag: String = "slider",
  thumbTestTag: String = "thumb",
) = Column {
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
    value = value,
    onValueChange = onValueChange,
    steps = steps,
    valueRange = valueRange,
    thumb = {
      Box(
        Modifier
          .size(ButtonDefaults.IconSize)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary)
          .testTag(thumbTestTag),
      )
    },
    track = { state ->
      SliderDefaults.Track(
        sliderState = state,
        drawStopIndicator = null,
        drawTick = { _, _ -> },
        modifier = Modifier.height(8.dp),
      )
    },
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .testTag(sliderTestTag)
      .semantics { testTagsAsResourceId = true },
  )
}

typealias FloatRange = ClosedFloatingPointRange<Float>
