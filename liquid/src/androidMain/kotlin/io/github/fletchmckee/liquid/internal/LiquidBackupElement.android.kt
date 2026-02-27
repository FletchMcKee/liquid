// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState

internal class LiquidBackupElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidElement<LiquidBackupNode>(liquidState, block) {
  override fun create() = LiquidBackupNode(liquidState, block)
}

internal class LiquidBackupNode(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidNode(liquidState, block) {
  private val canUseRenderEffect = Build.VERSION.SDK_INT >= 31
  private var cachedColorFilter: ColorFilter? = null

  override val invalidateFlags: Int = when {
    canUseRenderEffect -> Fields.PreTiramisuInvalidateFlags
    else -> Fields.PreSnowConeInvalidateFlags
  }

  override val renderEffectFlags: Int = when {
    canUseRenderEffect -> Fields.Frost
    else -> 0 // No render effect
  }

  override fun inspectDirtyFields() = with(reusableScope) {
    // The mutatedFields dirty tracker gets cleaned in super, so we need to invalidate here.
    if (mutatedFields has (Fields.Saturation or Fields.Contrast)) {
      cachedColorFilter = createColorFilter(
        saturation = saturation,
        contrast = contrast,
      )
    }
  }

  override fun ContentDrawScope.applyAdditionalEffects(
    layer: GraphicsLayer,
    drawBlock: () -> Unit,
  ) {
    val shapeOutline = reusableScope.shape.createOutline(size, layoutDirection, this)
    val shapePath = shapeOutline.asPath()
    layer.colorFilter = cachedColorFilter

    clipPath(shapePath) { drawBlock() }

    if (reusableScope.tint.isSpecified) {
      drawOutline(
        outline = shapeOutline,
        color = reusableScope.tint,
        style = Fill,
      )
    }

    if (reusableScope.edge > 0f) {
      drawBackupEdgeEffect(shapePath)
    }
  }

  @RequiresApi(31)
  override fun createRenderEffect(): RenderEffect? = with(reusableScope) {
    if (frostRadius <= 0f || size.isUnspecified) return null

    return BlurEffect(
      radiusX = frostRadius,
      radiusY = frostRadius,
      edgeTreatment = TileMode.Clamp,
    )
  }

  private fun createColorFilter(
    saturation: Float,
    contrast: Float,
  ): ColorFilter? = when {
    saturation == 1f && contrast == 1f -> null

    else -> {
      val compositeMatrix = ColorMatrix().apply { setToSaturation(saturation) }
      compositeMatrix.timesAssign(ColorMatrix().apply { setContrast(contrast) })
      ColorFilter.colorMatrix(compositeMatrix)
    }
  }
}

private fun Outline.asPath(): Path = when (this) {
  is Outline.Rectangle -> Path().apply { addRect(rect) }
  is Outline.Rounded -> Path().apply { addRoundRect(roundRect) }
  is Outline.Generic -> path
}

// This won't be that accurate, but we should at least provide an edge-like inner border using gradients
// if the user provided a value.
private fun ContentDrawScope.drawBackupEdgeEffect(shapePath: Path) = clipPath(shapePath) {
  val strokeWidth = 4.dp.toPx()
  val radius = size.minDimension
  // Light at topLeft corner
  drawPath(
    path = shapePath,
    brush = Brush.radialGradient(
      colors = listOf(Color(0x4DFFFFFF), Color.Transparent),
      center = Offset.Zero,
      radius = radius,
    ),
    style = Stroke(width = strokeWidth),
  )

  // Light at bottomRight corner
  drawPath(
    path = shapePath,
    brush = Brush.radialGradient(
      colors = listOf(Color(0x4DFFFFFF), Color.Transparent),
      center = Offset(size.width, size.height),
      radius = radius,
    ),
    style = Stroke(width = strokeWidth),
  )
}

private fun ColorMatrix.setContrast(contrast: Float) {
  val translate = 0.5f * (1f - contrast) * 255f
  setToScale(
    redScale = contrast,
    greenScale = contrast,
    blueScale = contrast,
    alphaScale = 1f,
  )
  this[0, 4] = translate // red offset
  this[1, 4] = translate // green offset
  this[2, 4] = translate // blue offset
}
