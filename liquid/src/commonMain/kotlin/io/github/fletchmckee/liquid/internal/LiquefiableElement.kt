// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.unit.toSize
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
    if (other !is LiquefiableElement) return false
    // Unnecessary to perform structural equality checks.
    return liquidState === other.liquidState
  }

  override fun hashCode(): Int = liquidState.hashCode()

  override fun InspectorInfo.inspectableProperties() {
    name = "Liquefiable"
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
  override val traverseKey: Any = LiquefiableKey

  // Internal as the LiquidNode needs to access it for filtering out the nearest ancestor.
  internal val liquefiable = Liquefiable()

  private fun obtainGraphicsLayer() = liquefiable.layer?.takeUnless { it.isReleased }
    ?: currentValueOf(LocalGraphicsContext)
      .createGraphicsLayer()
      .also { liquefiable.layer = it }

  override val shouldAutoInvalidate: Boolean = false

  override fun onAttach() {
    liquidState.liquefiables += liquefiable
  }

  override fun onDetach() = Snapshot.withMutableSnapshot {
    liquidState.liquefiables -= liquefiable
    liquefiable.layer?.let { currentValueOf(LocalGraphicsContext).releaseGraphicsLayer(it) }
    liquefiable.layer = null
    liquefiable.boundsOnScreen = Rect.Zero
  }

  override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
    liquefiable.boundsOnScreen = Rect(
      offset = coordinates.liquidPositionOnScreen(),
      size = coordinates.size.toSize(),
    )
  }

  override fun ContentDrawScope.draw() {
    if (size.minDimension < 1f) {
      drawContent()
      return
    }
    // Prevents double reads with the mutableState `liquefiable.layer` when `createGraphicsLayer` is called.
    val contentLayer = Snapshot.withoutReadObservation { obtainGraphicsLayer() }
    // Record the content into the layer
    contentLayer.record { this@draw.drawContent() }
    // No need to call drawContent since we did so in the recording.
    drawLayer(contentLayer)
  }
}
