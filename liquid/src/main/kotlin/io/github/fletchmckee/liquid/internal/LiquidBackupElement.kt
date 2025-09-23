// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
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

// This file can be deleted as soon as our minSdk is 33, so maybe by 2035.
internal class LiquidBackupElement(
  private val liquidState: LiquidState,
  private val block: LiquidScope.() -> Unit,
) : ModifierNodeElement<LiquidBackupNode>() {
  override fun create() = LiquidBackupNode(liquidState, block)

  override fun update(node: LiquidBackupNode) {
    node.liquidState = liquidState
    node.block = block
    node.invalidateLiquidBlock()
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "LiquidBackup"
    properties["block"] = block
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LiquidBackupElement) return false
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

internal class LiquidBackupNode(
  var liquidState: LiquidState?,
  var block: LiquidScope.() -> Unit,
) : Modifier.Node(),
  GlobalPositionAwareModifierNode,
  DrawModifierNode,
  CompositionLocalConsumerModifierNode,
  ObserverModifierNode {
  private val canUseRenderEffect = Build.VERSION.SDK_INT >= 31
  private val reusableScope = LiquidScopeImpl()
  private var cachedLayer: GraphicsLayer? = null

  internal fun invalidateLiquidBlock() {
    if (!isAttached) return

    block(reusableScope)

    // We only record if 31+, avoid unnecessary traversals if lower.
    if (canUseRenderEffect) {
      // Allows nodes to be both a liquefiable and liquid node while preventing recursive draws.
      val ancestor = (findNearestAncestor(LiquefiableNode.LiquefiableKey) as? LiquefiableNode)?.liquefiable
      reusableScope.liquefiables = liquidState?.liquefiables
        .orEmpty()
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
    val shouldInvalidate = reusableScope.mutatedFields has when {
      canUseRenderEffect -> Fields.PreTiramisuInvalidateFlags
      else -> Fields.PreSnowConeInvalidateFlags
    }

    if (shouldInvalidate) {
      // Clear the blur effect if frost changed (only relevant for API 31+)
      reusableScope.renderEffect = reusableScope.renderEffect.takeUnless { canUseRenderEffect && reusableScope.mutatedFields has Fields.Frost }
      invalidateDraw()
    }
  }

  // We handle all necessary invalidations in LiquidScopeImpl.
  override val shouldAutoInvalidate: Boolean = false

  override fun onAttach() = observeReads(::invalidateLiquidBlock)

  override fun onDetach() {
    cachedLayer?.let { layer -> currentValueOf(LocalGraphicsContext).releaseGraphicsLayer(layer) }
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
    try {
      val layer = obtainGraphicsLayer()
      reusableScope.density = currentValueOf(LocalDensity)
      drawBackupLiquidEffect(layer, reusableScope)
    } finally {
      reusableScope.mutatedFields = 0
    }
  }
}
