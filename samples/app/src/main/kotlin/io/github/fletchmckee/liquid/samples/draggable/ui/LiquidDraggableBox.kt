// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.draggable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.samples.draggable.utils.blendMode
import io.github.fletchmckee.liquid.samples.draggable.utils.safeShadow
import io.github.fletchmckee.liquid.samples.draggable.utils.thenIf
import kotlin.math.roundToInt

@Composable
fun BoxScope.LiquidDraggableBox(
  liquidState: LiquidState,
  frostProvider: () -> Float,
  refractionProvider: () -> Float,
  curveProvider: () -> Float,
  sharpProvider: () -> Float,
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(25),
  useGlass: Boolean = true,
  boxSize: DpSize = DpSize(width = 200.dp, height = 200.dp),
  shaderBrush: ShaderBrush = rememberShaderBrush(),
) {
  var dragOffset by remember { mutableStateOf(Offset.Zero) }

  Box(
    modifier
      .testTag("liquidDraggableBox")
      .semantics { testTagsAsResourceId = true }
      .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
      .size(boxSize)
      .align(Alignment.Center)
      .zIndex(2f)
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          val clampedX = dragOffset.x + dragAmount.x
          val clampedY = dragOffset.y + dragAmount.y
          dragOffset = Offset(clampedX, clampedY)
        }
      }
      .safeShadow(elevation = 4.dp, shape = shape)
      .thenIf(useGlass) {
        liquid(
          liquidState = liquidState,
          frost = frostProvider().dp,
          shape = shape,
          refraction = refractionProvider(),
          curve = curveProvider(),
          sharp = sharpProvider(),
        )
      }
      .background(brush = shaderBrush, shape = shape),
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
        .padding(12.dp)
        .blendMode(BlendMode.Difference),
    )
  }
}

@Composable
private fun rememberShaderBrush(
  colors: List<Color> = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
): ShaderBrush = remember(colors) {
  object : ShaderBrush() {
    override fun createShader(size: Size): Shader = LinearGradientShader(
      colors = colors,
      from = Offset.Zero,
      to = Offset(size.width, size.height),
    )
  }
}

val OffsetSaver = Saver<Offset, Pair<Float, Float>>(
  save = { it.x to it.y },
  restore = { Offset(it.first, it.second) },
)
