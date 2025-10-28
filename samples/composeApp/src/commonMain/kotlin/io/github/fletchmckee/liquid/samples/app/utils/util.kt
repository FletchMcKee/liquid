// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.utils

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

internal expect fun formatFloat(value: Float, format: String): String

// Used for benchmarks so that we can compare performance with none of the library's effects added.
internal fun Modifier.thenIf(
  condition: Boolean,
  block: Modifier.() -> Modifier,
): Modifier = if (condition) this.block() else this

internal fun Modifier.blendMode(blendMode: BlendMode): Modifier = drawWithCache {
  val layer = obtainGraphicsLayer()
  layer.apply {
    record { drawContent() }
    this.blendMode = blendMode
  }

  onDrawWithContent { drawLayer(layer) }
}

internal fun Modifier.drag(
  dragProvider: () -> Offset,
  onDragChange: (Offset) -> Unit,
): Modifier = this then Modifier
  .offset { IntOffset(dragProvider().x.roundToInt(), dragProvider().y.roundToInt()) }
  .pointerInput(Unit) {
    detectDragGestures { change, dragAmount ->
      change.consume()
      val x = dragProvider().x + dragAmount.x
      val y = dragProvider().y + dragAmount.y
      onDragChange(Offset(x, y))
    }
  }

@Composable
fun rememberShaderBrush(
  colors: List<Color> = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surfaceTint),
): ShaderBrush = remember(colors) {
  object : ShaderBrush() {
    override fun createShader(size: Size): Shader = LinearGradientShader(
      colors = colors,
      from = Offset(size.width / 2f, 0f),
      to = Offset(size.width / 2f, size.height),
    )
  }
}

internal fun Int.toPicsumId() = when (this) {
  76 -> 110
  87 -> 111
  95 -> 112
  else -> this
}.plus(10)
