// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastRoundToInt
import io.github.fletchmckee.liquid.Liquefiable
import io.github.fletchmckee.liquid.LiquidState

internal class LiquefiableElement(
  private val liquidState: LiquidState,
) : ModifierNodeElement<LiquefiableNode>() {
  override fun create(): LiquefiableNode = LiquefiableNode(liquidState)

  override fun update(node: LiquefiableNode) {
    node.liquidState = liquidState
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as LiquefiableElement

    return liquidState == other.liquidState
  }

  override fun hashCode(): Int = liquidState.hashCode()

  override fun InspectorInfo.inspectableProperties() {
    name = "liquefiable"
  }
}

internal class LiquefiableNode(
  var liquidState: LiquidState,
) : Modifier.Node(),
  CompositionLocalConsumerModifierNode,
  DrawModifierNode,
  GlobalPositionAwareModifierNode,
  TraversableNode {

  companion object LiquefiableKey

  override val traverseKey: Any
    get() = LiquefiableKey

  internal val liquefiable = Liquefiable()

  override val shouldAutoInvalidate: Boolean = false

  override fun onAttach() = liquidState.addLiquefiable(liquefiable)

  override fun onDetach() {
    liquidState.removeLiquefiable(liquefiable)
    liquefiable.boundsOnScreen = Rect.Zero
    liquefiable.layer?.let { layer ->
      currentValueOf(LocalGraphicsContext).releaseGraphicsLayer(layer)
    }
    liquefiable.layer = null
  }

  override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
    liquefiable.boundsOnScreen = Rect(coordinates.positionOnScreen(), coordinates.size.toSize())
  }

  override fun ContentDrawScope.draw() {
    if (!isAttached) return

    when {
      size.minDimension.fastRoundToInt() >= 1 -> {
        val contentLayer = liquefiable.layer?.takeUnless { it.isReleased }
          ?: currentValueOf(LocalGraphicsContext)
            .createGraphicsLayer()
            .also { liquefiable.layer = it }

        // Record the content into the layer
        contentLayer.record {
          this@draw.drawContent()
        }
        // No need to call drawContent since we did in the recording.
        drawLayer(contentLayer)
      }
      else -> drawContent()
    }
  }
}
