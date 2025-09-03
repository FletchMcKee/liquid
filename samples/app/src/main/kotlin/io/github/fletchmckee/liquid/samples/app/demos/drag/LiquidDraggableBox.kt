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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.samples.app.utils.blendMode
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush
import io.github.fletchmckee.liquid.samples.app.utils.safeShadow
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import kotlin.math.roundToInt

@Composable
fun BoxScope.LiquidDraggableBox(
  liquidState: LiquidState,
  frostProvider: () -> Float,
  refractionProvider: () -> Float,
  curveProvider: () -> Float,
  edgeProvider: () -> Float,
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(25),
  useLiquid: Boolean = true,
  colors: List<Color> = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
  shaderBrush: ShaderBrush = rememberShaderBrush(colors),
) {
  var dragOffset by remember { mutableStateOf(Offset.Zero) }

  Box(
    modifier
      .testTag("liquidDraggableBox")
      .semantics { testTagsAsResourceId = true }
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
      .safeShadow(elevation = 4.dp, shape = shape)
      .thenIf(useLiquid) {
        liquid(liquidState) {
          this.frost = frostProvider().dp
          this.shape = shape
          this.refraction = refractionProvider()
          this.curve = curveProvider()
          this.edge = edgeProvider()
        }
      }
      .background(brush = shaderBrush, shape = shape),
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
