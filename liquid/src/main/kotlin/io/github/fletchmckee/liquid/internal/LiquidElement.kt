// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RenderEffect.createRuntimeShaderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
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
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastRoundToInt
import io.github.fletchmckee.liquid.Liquefiable
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.internal.shaders.HorizontalFrostShader
import io.github.fletchmckee.liquid.internal.shaders.VerticalFrostShader
import kotlin.collections.orEmpty

@RequiresApi(33)
internal class LiquidElement(
  private val liquidState: LiquidState,
  private val frost: Dp,
  private val shape: Shape,
  private val refraction: Float,
  private val curve: Float,
  private val sharp: Float,
) : ModifierNodeElement<LiquidNode>() {

  override fun create() = LiquidNode(
    liquidState = liquidState,
    frost = frost,
    shape = shape,
    refraction = refraction,
    curve = curve,
    sharp = sharp,
  )

  override fun update(node: LiquidNode) {
    node.liquidState = liquidState
    node.frost = frost
    node.shape = shape
    node.refraction = refraction
    node.curve = curve
    node.sharp = sharp
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "Liquid"
    properties["frost"] = frost
    properties["shape"] = shape
    properties["refraction"] = refraction
    properties["curve"] = curve
    properties["sharp"] = sharp
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as LiquidElement

    if (liquidState != other.liquidState) return false
    if (frost != other.frost) return false
    if (shape != other.shape) return false
    if (refraction != other.refraction) return false
    if (curve != other.curve) return false
    if (sharp != other.sharp) return false

    return true
  }

  override fun hashCode(): Int {
    var result = liquidState.hashCode()
    result = 31 * result + frost.hashCode()
    result = 31 * result + shape.hashCode()
    result = 31 * result + refraction.hashCode()
    result = 31 * result + curve.hashCode()
    result = 31 * result + sharp.hashCode()
    return result
  }
}

@RequiresApi(33)
internal class LiquidNode(
  var liquidState: LiquidState?,
  var frost: Dp,
  var shape: Shape,
  var refraction: Float,
  var curve: Float,
  var sharp: Float,
) : Modifier.Node(),
  GlobalPositionAwareModifierNode,
  DrawModifierNode,
  CompositionLocalConsumerModifierNode,
  TraversableNode,
  ObserverModifierNode {

  companion object LiquidKey

  override val traverseKey: Any
    get() = LiquidKey

  private val liquidShader = RuntimeShader(LiquidShaderV2)
  private val horizontalShader = RuntimeShader(HorizontalFrostShader)
  private val verticalShader = RuntimeShader(VerticalFrostShader)

  private var cachedLayer: GraphicsLayer? = null
  private var bounds = Rect.Zero
  private var lastBounds = Rect.Zero
  private var liquefiables: List<Liquefiable> = emptyList()

  // Eventually this should be set to false, not quite there yet though.
  override val shouldAutoInvalidate: Boolean = true

  override fun onAttach() = observeReads(::invalidateIfNeeded)

  override fun onDetach() {
    cachedLayer?.let { layer -> currentValueOf(LocalGraphicsContext).releaseGraphicsLayer(layer) }
    cachedLayer = null
    liquefiables = emptyList()
    bounds = Rect.Zero
    lastBounds = Rect.Zero
    super.onDetach()
  }

  private fun invalidateIfNeeded() {
    if (!isAttached) return

    val ancestor = (findNearestAncestor(LiquefiableNode.LiquefiableKey) as? LiquefiableNode)?.liquefiable
    // This has room for improvement, but it allows nodes to be both a content and effect node while preventing infinite draws if a
    // parent `liquefiable` node has child `liquid` nodes.
    val newSides = liquidState?.liquefiables
      .orEmpty()
      .asSequence()
      .filterNot { it == ancestor }
      .toList()

    val shouldInvalidate = newSides != liquefiables || bounds != lastBounds

    liquefiables = newSides
    lastBounds = bounds

    if (shouldInvalidate) {
      invalidateDraw()
    }
  }

  override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
    if (!isAttached) return

    val position = coordinates.positionOnScreen()
    val size = coordinates.size.toSize()
    // For a uniform blur, we have to sample outside of the node's actual size with the blur radius as padding.
    val padding = with(requireDensity()) { frost.toPx() }
    bounds = Rect(
      left = position.x - padding,
      top = position.y - padding,
      right = position.x + size.width + padding,
      bottom = position.y + size.height + padding,
    )

    invalidateIfNeeded()
  }

  override fun ContentDrawScope.draw() {
    if (!isAttached) return

    if (size.minDimension.fastRoundToInt() < 1) {
      drawContent()
      return
    }

    val layer = cachedLayer?.takeUnless { it.isReleased }
      ?: currentValueOf(LocalGraphicsContext)
        .createGraphicsLayer()
        .also { cachedLayer = it }

    val density = currentValueOf(LocalDensity)
    val cornerRadius = shape.cornerRadiusPx(size, density)
    val frostRadius = with(density) { frost.toPx() }

    if (liquefiables.isNotEmpty()) {
      layer.record(bounds.size.toIntSize()) {
        liquefiables
          .asSequence()
          .filter { bounds.overlaps(it.boundsOnScreen) }
          .forEach { liquefiable ->
            liquefiable.layer
              ?.takeUnless { it.isReleased || it.size.isEmpty }
              ?.let { liquefiableLayer ->
                // Position content where it should appear on screen
                val (x, y) = liquefiable.boundsOnScreen.topLeft.orZero - bounds.topLeft.orZero
                translate(x, y) {
                  drawLayer(liquefiableLayer)
                }
              }
          }
      }
    }

    liquidShader.setLiquidUniforms(
      bounds = bounds,
      frostRadius = frostRadius,
      cornerRadius = cornerRadius,
      refraction = refraction,
      curve = curve,
      sharp = sharp,
    )

    val renderEffect = createRuntimeShaderEffect(liquidShader, "content")
      .configureRenderEffect(
        horizontalShader = horizontalShader,
        verticalShader = verticalShader,
        frostRadius = frostRadius,
        cornerRadius = cornerRadius,
        bounds = bounds,
      )

    layer.clip = cornerRadius > 0f
    layer.renderEffect = renderEffect
    translate(-frostRadius, -frostRadius) {
      drawLayer(layer)
    }
    drawContent()
  }

  override fun onObservedReadsChanged() = invalidateIfNeeded()
}
