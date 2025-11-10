// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.layer.GraphicsLayer
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

  override val invalidateFlags: Int = when {
    canUseRenderEffect -> Fields.PreTiramisuInvalidateFlags
    else -> Fields.PreSnowConeInvalidateFlags
  }

  override val renderEffectFlags: Int = when {
    canUseRenderEffect -> Fields.Frost
    else -> 0 // No render effect
  }

  override fun ContentDrawScope.applyAdditionalEffects(
    layer: GraphicsLayer,
    drawBlock: () -> Unit,
  ) {
    val shapeOutline = reusableScope.shape.createOutline(size, layoutDirection, this)
    val shapePath = shapeOutline.asPath()

    layer.colorFilter = (reusableScope.saturation != 1f)
      .takeIf { it }
      ?.let {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(reusableScope.saturation) })
      }

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
    if (frostRadius <= 0f && size.isSpecified) return null

    return BlurEffect(
      radiusX = frostRadius,
      radiusY = frostRadius,
      edgeTreatment = TileMode.Clamp,
    )
  }
}
