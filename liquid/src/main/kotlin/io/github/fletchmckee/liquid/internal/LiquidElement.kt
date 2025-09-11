// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
import io.github.fletchmckee.liquid.LiquidScope
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.internal.shaders.HorizontalFrostShader
import io.github.fletchmckee.liquid.internal.shaders.LiquidShader
import io.github.fletchmckee.liquid.internal.shaders.VerticalFrostShader

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

internal class LiquidNode(
  var liquidState: LiquidState,
  var block: LiquidScope.() -> Unit,
) : Modifier.Node(),
  GlobalPositionAwareModifierNode,
  DrawModifierNode,
  CompositionLocalConsumerModifierNode,
  ObserverModifierNode {
  // RuntimeShaders are created once per node instance rather than in draw() to avoid expensive native allocations.
  @delegate:RequiresApi(33)
  @get:RequiresApi(33)
  private val liquidShader by lazy(LazyThreadSafetyMode.NONE) { RuntimeShader(LiquidShader) }

  @delegate:RequiresApi(33)
  @get:RequiresApi(33)
  private val horizontalShader by lazy(LazyThreadSafetyMode.NONE) { RuntimeShader(HorizontalFrostShader) }

  @delegate:RequiresApi(33)
  @get:RequiresApi(33)
  private val verticalShader by lazy(LazyThreadSafetyMode.NONE) { RuntimeShader(VerticalFrostShader) }

  private val canUseRuntimeShaders = Build.VERSION.SDK_INT >= 33
  private val canUserRenderEffect = Build.VERSION.SDK_INT >= 31

  @VisibleForTesting
  internal val reusableScope = LiquidScopeImpl()

  private var cachedLayer: GraphicsLayer? = null

  internal fun invalidateLiquidBlock() {
    if (!isAttached) return

    block(reusableScope)

    // No liquefiables are recorded for API 30 and lower, avoid unnecessary traversals and filtering.
    if (canUserRenderEffect) {
      // Allows nodes to be both a liquefiable and liquid node while preventing recursive draws.
      val ancestor = (findNearestAncestor(LiquefiableNode.LiquefiableKey) as? LiquefiableNode)?.liquefiable
      reusableScope.liquefiables = liquidState.liquefiables
        .asSequence()
        .filter { it != ancestor }
        .toList()
    }

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

    reusableScope.positionOnScreen = coordinates.positionOnScreen()
    reusableScope.size = coordinates.size.toSize()

    invalidateDrawIfNeeded()
  }

  override fun ContentDrawScope.draw() {
    if (size.minDimension < 1f) {
      drawContent()
      return
    }

    try {
      val layer = obtainGraphicsLayer()
      if (!canUseRuntimeShaders) {
        drawBackupLiquidEffect(layer, reusableScope)
        return
      }

      reusableScope.density = currentValueOf(LocalDensity)
      // For a uniform frost, we sample outside of the node's actual size with the frost radius as padding.
      val padding = reusableScope.frostRadius
      recordLiquefiablesIntoLayer(layer, reusableScope)

      layer.clip = reusableScope.shape != RectangleShape
      // We can avoid creating the frost shaders altogether by only accessing the shaders if a non-zero frost is provided.
      layer.renderEffect = when {
        // May need to change this to <= 0f, but I see no point in blurring if it's a single pixel.
        padding < 1f -> reusableScope.obtainLiquidRenderEffect(liquidShader)
        else -> reusableScope.obtainLiquidFrostRenderEffect(
          liquidShader = liquidShader,
          horizontalShader = horizontalShader,
          verticalShader = verticalShader,
        )
      }
      // Need to translate topLeft to account for the frostRadius padding we've added for blur sampling.
      translate(-padding, -padding) { drawLayer(layer) }
      // Necessary to call this since it isn't part of the recording.
      drawContent()
    } finally {
      // Set it back to clean.
      reusableScope.mutatedFields = 0
    }
  }
}
