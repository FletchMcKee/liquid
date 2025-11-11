// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.drag

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.samples.app.nodes.testTagsAsResourceId
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.blendMode
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import kotlin.math.roundToInt

@Composable
fun BoxScope.LiquidDraggableBox(
  liquidState: LiquidState,
  frostProvider: () -> Float,
  refractionProvider: () -> Float,
  curveProvider: () -> Float,
  edgeProvider: () -> Float,
  saturationProvider: () -> Float,
  shapeProvider: () -> Shape,
  dispersionProvider: () -> Float,
  modifier: Modifier = Modifier,
  shaderBrush: ShaderBrush = rememberDiagonalShaderBrush(),
  initialYOffset: Dp = (-150).dp,
) {
  val useLiquid = LocalUseLiquid.current
  val density = LocalDensity.current
  var dragOffset by remember {
    mutableStateOf(Offset(x = 0f, y = with(density) { initialYOffset.toPx() }))
  }

  Box(
    modifier
      .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
      .size(200.dp)
      .align(Alignment.Center)
      .zIndex(2f)
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          val x = dragOffset.x + dragAmount.x
          val y = dragOffset.y + dragAmount.y
          dragOffset = Offset(x, y)
        }
      }
      .shadow(elevation = 4.dp, shape = shapeProvider())
      .thenIf(useLiquid) {
        liquid(liquidState) {
          frost = frostProvider().dp
          shape = shapeProvider()
          refraction = refractionProvider()
          curve = curveProvider()
          edge = edgeProvider()
          saturation = saturationProvider()
          dispersion = dispersionProvider()
        }
      } // Brushes aren't supported in liquid at the moment but may be added later.
      .background(brush = shaderBrush, shape = shapeProvider())
      .testTag("liquidDraggableBox")
      .testTagsAsResourceId(true),
  ) {
    Text(
      text = "Drag",
      color = Color.White,
      style = MaterialTheme.typography.headlineLarge.merge(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
      ),
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter)
        .padding(12.dp)
        // Helps improve the text legibility on light surfaces.
        .blendMode(BlendMode.Difference),
    )
  }
}

@Composable
private fun rememberDiagonalShaderBrush(
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
