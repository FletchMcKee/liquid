// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.annotation.VisibleForTesting
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.unit.toSize
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

// We could alter the logic to use a shared LiquidNode between all targets, but this would mean the
// RuntimeShaders would have to be created in the draw pass. This allows us to create them once per node.
internal expect fun liquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
): AbstractLiquidElement

// The skiko targets need to use `positionInWindow` instead of `positionOnScreen`.
internal expect fun LayoutCoordinates.liquidPositionOnScreen(): Offset

internal abstract class AbstractLiquidElement(
  protected val liquidState: LiquidState,
  protected val block: LiquidScope.() -> Unit,
) : ModifierNodeElement<AbstractLiquidNode>() {

  override fun InspectorInfo.inspectableProperties() {
    name = "Liquid"
    properties["block"] = block
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AbstractLiquidElement) return false
    // Unnecessary to perform structural equality checks.
    if (liquidState !== other.liquidState) return false
    if (block !== other.block) return false

    return true
  }

  override fun hashCode(): Int {
    var result = liquidState.hashCode()
    result = 31 * result + block.hashCode()
    return result
  }
}

internal abstract class AbstractLiquidNode(
  var liquidState: LiquidState,
  var block: LiquidScope.() -> Unit,
) : Modifier.Node(),
  GlobalPositionAwareModifierNode,
  DrawModifierNode,
  CompositionLocalConsumerModifierNode,
  ObserverModifierNode {

  @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
  internal val reusableScope = LiquidScopeImpl()
  private val matrix = Matrix()
  private var cachedLayer: GraphicsLayer? = null

  protected abstract fun invalidateDrawIfNeeded()

  protected abstract fun ContentDrawScope.drawLiquidEffects(
    layer: GraphicsLayer,
    drawBlock: () -> Unit,
  )

  internal fun invalidateLiquidBlock() {
    if (!isAttached) return

    block(reusableScope)

    // Allows nodes to be both a liquefiable and liquid node while preventing recursive draws.
    val ancestor = (findNearestAncestor(LiquefiableNode.LiquefiableKey) as? LiquefiableNode)?.liquefiable
    reusableScope.liquefiables = liquidState.liquefiables
      .asSequence()
      .filter { it != ancestor }
      .toList()

    invalidateDrawIfNeeded()
  }

  private fun obtainGraphicsLayer() = cachedLayer?.takeUnless { it.isReleased }
    ?: currentValueOf(LocalGraphicsContext)
      .createGraphicsLayer()
      .also { cachedLayer = it }

  // We handle all necessary invalidations in LiquidScopeImpl.
  override val shouldAutoInvalidate: Boolean = false

  override fun onAttach() = observeReads(::invalidateLiquidBlock)

  override fun onDetach() {
    cachedLayer?.let { currentValueOf(LocalGraphicsContext).releaseGraphicsLayer(it) }
    cachedLayer = null
    reusableScope.reset()
  }

  override fun onObservedReadsChanged() = invalidateLiquidBlock()

  override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
    if (!isAttached) return

    matrix.reset()
    coordinates.transformToScreen(matrix)
    val scaleX = matrix.values[Matrix.ScaleX]
    val scaleY = matrix.values[Matrix.ScaleY]
    val skewX = matrix.values[Matrix.SkewX]
    val skewY = matrix.values[Matrix.SkewY]
    val scaleXMagnitude = sqrt(scaleX * scaleX + skewY * skewY)
    val scaleYMagnitude = sqrt(skewX * skewX + scaleY * scaleY)
    reusableScope.positionOnScreen = coordinates.liquidPositionOnScreen()
    reusableScope.size = coordinates.size.toSize()
    reusableScope.inverseScaleX = if (scaleXMagnitude > 0f) 1f / scaleXMagnitude else 0f
    reusableScope.inverseScaleY = if (scaleYMagnitude > 0f) 1f / scaleYMagnitude else 0f
    reusableScope.inverseRotationZ = -RadiansToDegrees * atan2(skewY, scaleX)
    reusableScope.boundsInRoot = coordinates.boundsInRoot()
    reusableScope.screenBounds = Rect(Offset.Zero, coordinates.findRootCoordinates().size.toSize())

    invalidateDrawIfNeeded()
  }

  override fun ContentDrawScope.draw() {
    if (size.minDimension < 1f) {
      drawContent()
      return
    }

    try {
      val layer = obtainGraphicsLayer()
      reusableScope.density = currentValueOf(LocalDensity)
      recordLiquefiablesIntoLayer(layer, reusableScope)

      val padding = -reusableScope.frostRadius
      drawLiquidEffects(layer) {
        translate(padding, padding) { drawLayer(layer) }
      }

      drawContent()
    } finally {
      reusableScope.reset()
    }
  }
}

private const val RadiansToDegrees = (180.0 / PI).toFloat()
