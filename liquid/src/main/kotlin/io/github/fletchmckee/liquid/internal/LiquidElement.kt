// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.unit.toSize
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.internal.shaders.HorizontalFrostShader
import io.github.fletchmckee.liquid.internal.shaders.LiquidShader
import io.github.fletchmckee.liquid.internal.shaders.VerticalFrostShader
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

@RequiresApi(33)
internal class LiquidElement(
  private val liquidState: LiquidState,
  private val block: LiquidScope.() -> Unit,
) : ModifierNodeElement<LiquidNode>() {
  override fun create() = LiquidNode(liquidState, block)

  override fun update(node: LiquidNode) {
    node.liquidState = liquidState
    node.block = block
    node.invalidateLiquidBlock()
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "Liquid"
    properties["block"] = block
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LiquidElement) return false
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

@RequiresApi(33)
internal class LiquidNode(
  var liquidState: LiquidState,
  var block: LiquidScope.() -> Unit,
) : Modifier.Node(),
  GlobalPositionAwareModifierNode,
  DrawModifierNode,
  CompositionLocalConsumerModifierNode,
  ObserverModifierNode {
  // RuntimeShaders are created once per node instance rather than in draw() to avoid expensive native allocations.
  // Initially used lazy delegation, but creating these shaders immediately demonstrated performance improvements.
  private val liquidShader = RuntimeShader(LiquidShader)
  private val horizontalShader = RuntimeShader(HorizontalFrostShader)
  private val verticalShader = RuntimeShader(VerticalFrostShader)

  @VisibleForTesting
  internal val reusableScope = LiquidScopeImpl()
  private val matrix = Matrix()
  private var cachedLayer: GraphicsLayer? = null

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

  private fun invalidateDrawIfNeeded() {
    if (reusableScope.mutatedFields has Fields.InvalidateFlags) {
      invalidateDraw()
    }
  }

  // We handle all necessary invalidations in LiquidScopeImpl.
  override val shouldAutoInvalidate: Boolean = false

  // The `observeReads` call is critical here, otherwise we won't receive updates from LiquidScope/Liquefiable property mutations.
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
    reusableScope.positionOnScreen = coordinates.positionOnScreen()
    reusableScope.size = coordinates.size.toSize()
    reusableScope.scaleX = sqrt(scaleX * scaleX + skewY * skewY)
    reusableScope.scaleY = sqrt(skewX * skewX + scaleY * scaleY)
    reusableScope.rotationZ = RadiansToDegrees * atan2(skewY, scaleX)
    reusableScope.boundsInRoot = coordinates.boundsInRoot()

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
      // For a uniform frost, we sample outside of the node's actual size with the frost radius as padding.
      val padding = reusableScope.frostRadius
      recordLiquefiablesIntoLayer(layer, reusableScope)

      layer.renderEffect = reusableScope.obtainRenderEffect(
        liquidShader = liquidShader,
        horizontalShader = horizontalShader,
        verticalShader = verticalShader,
      )

      // Need to translate topLeft to account for the frostRadius padding we've added for blur sampling.
      translate(-padding, -padding) { drawLayer(layer) }
      // Necessary to call this since it isn't part of the recording.
      drawContent()
    } finally {
      // Set it back to clean.
      reusableScope.reset()
    }
  }
}

private const val RadiansToDegrees = (180.0 / PI).toFloat()
