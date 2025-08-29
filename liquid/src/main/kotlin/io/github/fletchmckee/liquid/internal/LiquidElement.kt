// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
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
import androidx.compose.ui.util.fastRoundToInt
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.internal.shaders.HorizontalFrostShader
import io.github.fletchmckee.liquid.internal.shaders.LiquidShader
import io.github.fletchmckee.liquid.internal.shaders.VerticalFrostShader
import io.github.fletchmckee.liquid.internal.shaders.createRenderEffect
import kotlin.collections.orEmpty

@RequiresApi(33)
internal class LiquidElement(
  private val liquidState: LiquidState,
  private val block: LiquidScope.() -> Unit,
) : ModifierNodeElement<LiquidNode>() {
  override fun create() = LiquidNode(
    liquidState = liquidState,
    block = block,
  )

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
  var liquidState: LiquidState?,
  var block: LiquidScope.() -> Unit,
) : Modifier.Node(),
  GlobalPositionAwareModifierNode,
  DrawModifierNode,
  CompositionLocalConsumerModifierNode,
  ObserverModifierNode {
  private val liquidShader = RuntimeShader(LiquidShader)
  private val horizontalShader = RuntimeShader(HorizontalFrostShader)
  private val verticalShader = RuntimeShader(VerticalFrostShader)

  @VisibleForTesting
  internal val reusableScope = LiquidScopeImpl()

  // Recreating the GraphicsLayer or RenderEffect causes many native allocations, so we're using an in-memory layer/effect.
  private var cachedLayer: GraphicsLayer? = null
  private var cachedRenderEffect: RenderEffect? = null

  internal fun invalidateLiquidBlock() {
    if (!isAttached) return

    block(reusableScope)

    val ancestor = (findNearestAncestor(LiquefiableNode.LiquefiableKey) as? LiquefiableNode)?.liquefiable
    // Allows nodes to be both a liquefiable and liquid node while preventing recursive draws.
    reusableScope.liquefiables = liquidState?.liquefiables
      .orEmpty()
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
      cachedRenderEffect = cachedRenderEffect.takeUnless { reusableScope.mutatedFields has Fields.RenderEffectFields }
      invalidateDraw()
    }
  }

  override val shouldAutoInvalidate: Boolean = false

  override fun onAttach() = observeReads(::invalidateLiquidBlock)

  override fun onDetach() {
    cachedLayer?.let { layer -> currentValueOf(LocalGraphicsContext).releaseGraphicsLayer(layer) }
    cachedLayer = null
    cachedRenderEffect = null
    reusableScope.reset()
  }

  override fun onObservedReadsChanged() = invalidateLiquidBlock()

  override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
    if (!isAttached) return

    reusableScope.positionOnScreen = coordinates.positionOnScreen()
    reusableScope.size = coordinates.size.toSize()

    invalidateDrawIfNeeded()
  }

  override fun ContentDrawScope.draw() {
    if (!isAttached) return

    if (size.minDimension.fastRoundToInt() < 1) {
      drawContent()
      return
    }

    try {
      val layer = obtainGraphicsLayer()

      val density = currentValueOf(LocalDensity)
      val cornerRadii = reusableScope.shape.cornerRadiiPx(size, density)
      val frostRadius = reusableScope.frost.toPx()
      // For a uniform frost, we sample outside of the node's actual size with the frost radius as padding.
      val bounds = reusableScope.paddedBounds(padding = frostRadius)

      recordLiquefiablesIntoLayer(
        layer = layer,
        liquefiables = reusableScope.liquefiables,
        bounds = bounds,
      )

      val renderEffect = cachedRenderEffect
        ?: createRenderEffect(
          liquidShader = liquidShader,
          horizontalShader = horizontalShader,
          verticalShader = verticalShader,
          bounds = bounds,
          frostRadius = frostRadius,
          cornerRadii = cornerRadii,
          reusableScope = reusableScope,
        ).also { cachedRenderEffect = it }

      layer.clip = reusableScope.shape != RectangleShape
      layer.renderEffect = renderEffect
      // Need to translate topLeft to account for the frostRadius padding we've added for blur sampling.
      translate(-frostRadius, -frostRadius) { drawLayer(layer) }
      // Necessary to call this since it isn't part of the recording.
      drawContent()
    } finally {
      reusableScope.mutatedFields = 0
    }
  }
}
