// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.samples.app.nodes.testTagsAsResourceId
import io.github.fletchmckee.liquid.samples.app.theme.LiquidShadow
import io.github.fletchmckee.liquid.samples.app.theme.LocalIsBenchmark
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.formatFloat
import io.github.fletchmckee.liquid.samples.app.utils.thenIf

@Composable
fun BoxScope.LiquidControls(
  liquidState: LiquidState,
  showSliders: Boolean,
  modifier: Modifier = Modifier,
  frostProvider: (() -> Float)? = null,
  onFrostChange: (Float) -> Unit = {},
  refractionProvider: (() -> Float)? = null,
  onRefractionChange: (Float) -> Unit = {},
  curveProvider: (() -> Float)? = null,
  onCurveChange: (Float) -> Unit = {},
  edgeProvider: (() -> Float)? = null,
  onEdgeChange: (Float) -> Unit = {},
  saturationProvider: (() -> Float)? = null,
  onSaturationChange: (Float) -> Unit = {},
  cornerPercentProvider: (() -> Int)? = null,
  onCornerPercentChange: (Int) -> Unit = {},
  dispersionProvider: (() -> Float)? = null,
  onDispersionChange: (Float) -> Unit = {},
  containerShape: Shape = RoundedCornerShape(8),
  containerColor: Color = MaterialTheme.colorScheme.surface,
  containerFrost: Dp = 15.dp,
  containerRefraction: Float = 0.08f,
  containerEdge: Float = 0.02f,
  useLiquid: Boolean = LocalUseLiquid.current,
) {
  // TODO: Add configuration change logic
  val isLandscape = false

  AnimatedVisibility(
    visible = showSliders,
    enter = fadeIn(tween(1000)) + expandVertically(tween(1000)),
    exit = fadeOut(tween(1000)) + shrinkVertically(tween(1000)),
    modifier = modifier
      .align(if (isLandscape) Alignment.CenterEnd else Alignment.BottomCenter)
      .widthIn(max = 600.dp)
      .wrapContentHeight()
      .padding(if (isLandscape) PaddingValues.Zero else WindowInsets.systemBars.asPaddingValues())
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
      .then(
        when {
          useLiquid ->
            Modifier
              .dropShadow(containerShape, LiquidShadow)
              .liquid(liquidState) {
                frost = containerFrost
                shape = containerShape
                curve = 0.15f
                refraction = containerRefraction
                edge = containerEdge
                tint = containerColor
              }
          else -> Modifier.background(containerColor, containerShape)
        },
      ),
  ) {
    LazyColumn(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      frostProvider?.let {
        item(key = "Frost") {
          LiquidSliderRow(
            text = "Frost",
            value = frostProvider(),
            onValueChange = onFrostChange,
            steps = 29,
            valueRange = 0f..30f,
            formatter = "%,.0f",
            sliderTestTag = "frostSlider",
            thumbTestTag = "frostThumb",
          )
        }
      }

      refractionProvider?.let {
        item(key = "Refraction") {
          LiquidSliderRow(
            text = "Refraction",
            value = refractionProvider(),
            onValueChange = onRefractionChange,
            steps = 49,
            valueRange = 0f..0.5f,
          )
        }
      }

      curveProvider?.let {
        item(key = "Curve") {
          LiquidSliderRow(
            text = "Curve",
            value = curveProvider(),
            onValueChange = onCurveChange,
            steps = 99,
            valueRange = 0f..1f,
          )
        }
      }

      edgeProvider?.let {
        item(key = "Edge") {
          LiquidSliderRow(
            text = "Edge",
            value = edgeProvider(),
            onValueChange = onEdgeChange,
            steps = 24,
            valueRange = 0.0f..0.25f,
          )
        }
      }

      saturationProvider?.let {
        item(key = "Saturation") {
          LiquidSliderRow(
            text = "Saturation",
            value = saturationProvider(),
            onValueChange = onSaturationChange,
            valueRange = 0f..2f,
          )
        }
      }

      cornerPercentProvider?.let {
        item(key = "Corner") {
          LiquidSliderRow(
            text = "Corner",
            value = cornerPercentProvider().toFloat(),
            onValueChange = { onCornerPercentChange(it.toInt()) },
            formatter = "%,.0f",
            steps = 49,
            valueRange = 0.0f..50f,
          )
        }
      }

      dispersionProvider?.let {
        item(key = "Dispersion") {
          LiquidSliderRow(
            text = "Dispersion",
            value = dispersionProvider(),
            onValueChange = onDispersionChange,
            steps = 99,
            valueRange = 0f..1f,
          )
        }
      }
    }
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
) = Row(
  modifier = modifier
    .fillMaxWidth()
    .padding(horizontal = 4.dp),
  verticalAlignment = Alignment.CenterVertically,
  horizontalArrangement = Arrangement.spacedBy(4.dp),
) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelLarge.copy(
      color = MaterialTheme.colorScheme.onBackground,
      fontSize = 14.sp,
    ),
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    textAlign = TextAlign.Start,
    modifier = Modifier.weight(0.25f),
  )

  PrimarySlider(
    value = value,
    onValueChange = onValueChange,
    steps = steps,
    valueRange = valueRange,
    thumbTestTag = thumbTestTag,
    modifier = Modifier
      .weight(0.63f)
      .testTag(sliderTestTag)
      .testTagsAsResourceId(true),
  )

  // In benchmarks we only want to measure LiquidScope property/UI performance and not text side effects.
  if (!LocalIsBenchmark.current) {
    Text(
      text = formatFloat(value, formatter),
      style = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 14.sp,
      ),
      maxLines = 1,
      textAlign = TextAlign.End,
      modifier = Modifier.weight(0.12f),
    )
  }
}
