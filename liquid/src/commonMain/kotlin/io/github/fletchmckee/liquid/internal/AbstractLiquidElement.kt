// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastFilter
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

// Nodes are separated so that RuntimeShader/RuntimeEffect can be created once.
internal expect fun liquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
): AbstractLiquidElement<out AbstractLiquidNode>

// `positionOnScreen()` doesn't return the same thing between Android and Skiko.
internal expect fun LayoutCoordinates.liquidPositionOnScreen(): Offset

internal abstract class AbstractLiquidElement<N : AbstractLiquidNode>(
  protected val liquidState: LiquidState,
  protected val block: LiquidScope.() -> Unit,
) : ModifierNodeElement<N>() {

  override fun update(node: N) {
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
    if (other !is AbstractLiquidElement<N>) return false
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
  private var cachedRenderEffect: RenderEffect? = null

  // Invoked when [renderEffectFlags] is mutated
  protected abstract fun createRenderEffect(): RenderEffect?
  // Invoked when [invalidateFlags] is mutated.
  protected open fun inspectDirtyFields() = Unit
  // Apply additional effects unrelated to the RenderEffect
  protected open fun ContentDrawScope.applyAdditionalEffects(
    layer: GraphicsLayer,
    drawBlock: () -> Unit,
  ) = drawBlock()

  protected open val invalidateFlags: Int = Fields.InvalidateFlags
  protected open val renderEffectFlags: Int = Fields.RenderEffectFields

  internal fun invalidateLiquidBlock() {
    if (!isAttached) return
    // Updating density/layoutDirection here prevents unnecessary RenderEffect invalidations in the draw pass.
    reusableScope.density = currentValueOf(LocalDensity)
    reusableScope.layoutDirection = currentValueOf(LocalLayoutDirection)
    block(reusableScope)

    // Changes to `liquefiables` should be tracked, not ancestors.
    val ancestor = Snapshot.withoutReadObservation {
      // Allows nodes to be both a liquefiable and liquid node while preventing recursive draws.
      (findNearestAncestor(LiquefiableNode.LiquefiableKey) as? LiquefiableNode)?.liquefiable
    }
    reusableScope.liquefiables = liquidState.liquefiables.fastFilter { it != ancestor }

    if (reusableScope.size.isSpecified) {
      invalidateDrawIfNeeded()
    }
  }

  private fun invalidateDrawIfNeeded() {
    if (reusableScope.mutatedFields has invalidateFlags) {
      inspectDirtyFields()

      if (reusableScope.mutatedFields has renderEffectFlags) {
        cachedRenderEffect = createRenderEffect()
      }

      reusableScope.clean()
      invalidateDraw()
    }
  }

  private fun obtainGraphicsLayer() = cachedLayer?.takeUnless { it.isReleased }
    ?: currentValueOf(LocalGraphicsContext)
      .createGraphicsLayer()
      .also { cachedLayer = it }

  // Performance optimization since we handle invalidations.
  override val shouldAutoInvalidate: Boolean = false

  override fun onAttach() = observeReads(::invalidateLiquidBlock)

  override fun onDetach() {
    cachedLayer?.let { currentValueOf(LocalGraphicsContext).releaseGraphicsLayer(it) }
    cachedLayer = null
    reusableScope.clean()
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

    invalidateDrawIfNeeded()
  }

  override fun ContentDrawScope.draw() {
    if (size.minDimension < 1f) {
      drawContent()
      return
    }

    val layer = obtainGraphicsLayer()
    recordLiquefiablesIntoLayer(layer, reusableScope)

    applyAdditionalEffects(layer) {
      layer.renderEffect = cachedRenderEffect
      drawLayer(layer)
    }

    drawContent()
  }
}

private const val RadiansToDegrees = (180.0 / PI).toFloat()
