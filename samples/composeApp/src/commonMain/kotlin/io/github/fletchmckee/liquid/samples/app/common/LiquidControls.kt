// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.ui.util.fastRoundToInt
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
internal fun BoxScope.LiquidControls(
  liquidState: LiquidState,
  showSliders: Boolean,
  liquidScopeManager: LiquidScopeManager,
  modifier: Modifier = Modifier,
  containerShape: Shape = RoundedCornerShape(8),
  containerColor: Color = MaterialTheme.colorScheme.surface,
  containerFrost: Dp = 15.dp,
  containerRefraction: Float = 0.08f,
  containerEdge: Float = 0.02f,
  useLiquid: Boolean = LocalUseLiquid.current,
  isLandscape: Boolean = false,
) {
  val expand = when {
    isLandscape -> expandHorizontally(tween(1000))
    else -> expandVertically(tween(1000))
  }
  val shrink = when {
    isLandscape -> shrinkHorizontally(tween(1000))
    else -> shrinkVertically(tween(1000))
  }
  AnimatedVisibility(
    visible = showSliders,
    enter = fadeIn(tween(1000)) + expand,
    exit = fadeOut(tween(1000)) + shrink,
    modifier = modifier
      .align(if (isLandscape) Alignment.BottomEnd else Alignment.BottomCenter)
      .wrapContentHeight()
      .padding(if (isLandscape) PaddingValues.Zero else WindowInsets.systemBars.asPaddingValues())
      .padding(16.dp)
      .thenIf(useLiquid) {
        liquefiable(liquidState)
      },
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth(if (isLandscape) 0.4f else 1f)
        .pointerInput(Unit) {
          detectDragGestures { change, dragAmount ->
            // Prevents swiping the HorizontalPager.
            change.consume()
          }
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
        )
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      if (!liquidScopeManager.frost.isNaN()) {
        item(key = "Frost") {
          LiquidSliderRow(
            text = "Frost",
            value = liquidScopeManager.frost,
            onValueChange = { liquidScopeManager.frost = it },
            steps = 29,
            valueRange = 0f..30f,
            formatter = "%,.0f",
            sliderTestTag = "frostSlider",
            thumbTestTag = "frostThumb",
          )
        }
      }

      if (!liquidScopeManager.refraction.isNaN()) {
        item(key = "Refraction") {
          LiquidSliderRow(
            text = "Refraction",
            value = liquidScopeManager.refraction,
            onValueChange = { liquidScopeManager.refraction = it },
            steps = 49,
            valueRange = 0f..0.5f,
          )
        }
      }

      if (!liquidScopeManager.curve.isNaN()) {
        item(key = "Curve") {
          LiquidSliderRow(
            text = "Curve",
            value = liquidScopeManager.curve,
            onValueChange = { liquidScopeManager.curve = it },
            steps = 99,
            valueRange = 0f..1f,
          )
        }
      }

      if (!liquidScopeManager.edge.isNaN()) {
        item(key = "Edge") {
          LiquidSliderRow(
            text = "Edge",
            value = liquidScopeManager.edge,
            onValueChange = { liquidScopeManager.edge = it },
            steps = 24,
            valueRange = 0.0f..0.25f,
          )
        }
      }

      if (!liquidScopeManager.saturation.isNaN()) {
        item(key = "Saturation") {
          LiquidSliderRow(
            text = "Saturation",
            value = liquidScopeManager.saturation,
            onValueChange = { liquidScopeManager.saturation = it },
            steps = 99,
            valueRange = 0f..2f,
          )
        }
      }

      if (liquidScopeManager.cornerPercent >= 0) {
        item(key = "Corner") {
          LiquidSliderRow(
            text = "Corner",
            value = liquidScopeManager.cornerPercent.toFloat(),
            onValueChange = { liquidScopeManager.cornerPercent = it.fastRoundToInt() },
            formatter = "%,.0f",
            steps = 49,
            valueRange = 0.0f..50f,
          )
        }
      }

      if (!liquidScopeManager.dispersion.isNaN()) {
        item(key = "Dispersion") {
          LiquidSliderRow(
            text = "Dispersion",
            value = liquidScopeManager.dispersion,
            onValueChange = { liquidScopeManager.dispersion = it },
            steps = 99,
            valueRange = 0f..1f,
          )
        }
      }

      if (!liquidScopeManager.contrast.isNaN()) {
        item(key = "Contrast") {
          LiquidSliderRow(
            text = "Contrast",
            value = liquidScopeManager.contrast,
            onValueChange = { liquidScopeManager.contrast = it },
            steps = 99,
            valueRange = 0f..2f,
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
