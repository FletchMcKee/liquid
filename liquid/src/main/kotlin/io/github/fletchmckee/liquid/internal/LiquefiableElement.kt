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
import io.github.fletchmckee.liquid.Liquid

internal class LiquefiableElement(
  private val liquid: Liquid,
) : ModifierNodeElement<LiquefiableNode>() {
  override fun create(): LiquefiableNode = LiquefiableNode(liquid)

  override fun update(node: LiquefiableNode) {
    node.liquid = liquid
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as LiquefiableElement

    return liquid == other.liquid
  }

  override fun hashCode(): Int = liquid.hashCode()

  override fun InspectorInfo.inspectableProperties() {
    name = "liquefiable"
    properties["liquid"] = liquid
  }
}

internal class LiquefiableNode(
  var liquid: Liquid,
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

  override fun onAttach() = liquid.addLiquefiable(liquefiable)

  override fun onDetach() {
    liquid.removeLiquefiable(liquefiable)
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

        // Draw the layer and then content.
        drawLayer(contentLayer)
        drawContent()
      }
      else -> drawContent()
    }
  }
}
