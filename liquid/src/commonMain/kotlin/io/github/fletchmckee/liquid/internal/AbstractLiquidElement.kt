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
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastFilter
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

// We have separate nodes so that Android and Skiko can create their RuntimeShader/RuntimeEffect
// in `init` rather than the draw pass as this has shown better performance.
internal expect fun liquidElement(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
): AbstractLiquidElement<out AbstractLiquidNode>

// The `positionOnScreen()` doesn't return the same thing between Android and Skiko.
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

  protected abstract fun createRenderEffect(): RenderEffect?

  // This method along with the open flags can be removed once our minSdk is 33+.
  protected open fun ContentDrawScope.applyAdditionalEffects(
    layer: GraphicsLayer,
    drawBlock: () -> Unit,
  ) = drawBlock()

  protected open val invalidateFlags: Int = Fields.InvalidateFlags

  protected open val renderEffectFlags: Int = Fields.RenderEffectFields

  internal fun invalidateLiquidBlock() {
    if (!isAttached) return
    // Our frostRadius calculations rely on density. Setting it here prevents unnecessary RenderEffect
    // invalidations in the draw pass.
    reusableScope.density = currentValueOf(LocalDensity)
    block(reusableScope)

    // Changes to `liquefiables` should be tracked, not ancestors.
    val ancestor = Snapshot.withoutReadObservation {
      // Allows nodes to be both a liquefiable and liquid node while preventing recursive draws.
      (findNearestAncestor(LiquefiableNode.LiquefiableKey) as? LiquefiableNode)?.liquefiable
    }
    reusableScope.liquefiables = liquidState.liquefiables.fastFilter { it != ancestor }

    // This avoids unnecessary invalidateDraw calls as this could be called multiple times before we have
    // a valid size and position.
    if (reusableScope.size.isSpecified) {
      invalidateDrawIfNeeded()
    }
  }

  private fun invalidateDrawIfNeeded() {
    if (reusableScope.mutatedFields has Fields.InvalidateFlags) {
      if (reusableScope.mutatedFields has Fields.RenderEffectFields) {
        cachedRenderEffect = createRenderEffect()
      }
      // It's important to call `clean()` after `createRenderEffect()` as the `createRenderEffect()`
      // relies on the `mutatedFields` tracker to know when it has to be reconfigured.
      reusableScope.clean()
      invalidateDraw()
    }
  }

  private fun obtainGraphicsLayer() = cachedLayer?.takeUnless { it.isReleased }
    ?: currentValueOf(LocalGraphicsContext)
      .createGraphicsLayer()
      .also { cachedLayer = it }

  // We handle all necessary invalidations in LiquidScopeImpl.
  override val shouldAutoInvalidate: Boolean = false

  // The `observeReads` call is critical here, otherwise we won't receive updates from
  // LiquidScope/Liquefiable property mutations.
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

    layer.renderEffect = cachedRenderEffect
    applyAdditionalEffects(layer) {
      drawLayer(layer)
    }

    // Necessary since it isn't part of the recording.
    drawContent()
  }
}

private const val RadiansToDegrees = (180.0 / PI).toFloat()
