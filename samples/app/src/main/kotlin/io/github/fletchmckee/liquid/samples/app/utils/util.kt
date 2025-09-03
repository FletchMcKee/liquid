// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.utils

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Used for benchmarks so that we can compare performance with none of the library's effects added.
internal fun Modifier.thenIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier = if (condition) this.block() else this

internal fun Modifier.blendMode(blendMode: BlendMode): Modifier = drawWithCache {
  val layer = obtainGraphicsLayer()
  layer.apply {
    record { drawContent() }
    this.blendMode = blendMode
  }

  onDrawWithContent { drawLayer(layer) }
}

// There's likely a way to not have the shadow appear underneath transparent content,
// but haven't figured that out yet.
internal fun Modifier.safeShadow(
  elevation: Dp = 4.dp,
  shape: Shape = RoundedCornerShape(25.dp),
) = this then when {
  Build.VERSION.SDK_INT >= 33 -> Modifier.shadow(elevation, shape)
  else -> Modifier
}

@Composable
internal fun rememberShaderBrush(
  colors: List<Color> = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.primary),
): ShaderBrush = remember(colors) {
  object : ShaderBrush() {
    override fun createShader(size: Size): Shader = LinearGradientShader(
      colors = colors,
      from = Offset.Zero,
      to = Offset(size.width, size.height),
    )
  }
}
