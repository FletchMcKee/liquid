// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import android.graphics.RenderEffect.createRuntimeShaderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.node.traverseAncestors
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.toSize
import io.github.fletchmckee.liquid.Liquefiable
import io.github.fletchmckee.liquid.Liquid
import kotlin.collections.orEmpty

@RequiresApi(33)
internal class LiquidElement(
  private val liquid: Liquid,
  private val frost: Dp,
  private val shape: Shape,
  private val lensRefraction: Float,
  private val lensCurvature: Float,
  private val sharp: Float,
  private val tint: Color,
) : ModifierNodeElement<LiquidNode>() {

  override fun create() = LiquidNode(
    liquid = liquid,
    frost = frost,
    shape = shape,
    lensRefraction = lensRefraction,
    lensCurvature = lensCurvature,
    sharp = sharp,
    tint = tint,
  )

  override fun update(node: LiquidNode) {
    node.liquid = liquid
    node.frost = frost
    node.shape = shape
    node.lensRefraction = lensRefraction
    node.lensCurvature = lensCurvature
    node.sharp = sharp
    node.tint = tint
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "Liquid"
    properties["liquid"] = liquid
    properties["frost"] = frost
    properties["shape"] = shape
    properties["lensRefraction"] = lensRefraction
    properties["lensCurvature"] = lensCurvature
    properties["sharp"] = sharp
    properties["tint"] = tint
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as LiquidElement

    if (liquid != other.liquid) return false
    if (frost != other.frost) return false
    if (shape != other.shape) return false
    if (lensRefraction != other.lensRefraction) return false
    if (lensCurvature != other.lensCurvature) return false
    if (sharp != other.sharp) return false
    if (tint != other.tint) return false

    return true
  }

  override fun hashCode(): Int {
    var result = liquid.hashCode()
    result = 31 * result + frost.hashCode()
    result = 31 * result + shape.hashCode()
    result = 31 * result + lensRefraction.hashCode()
    result = 31 * result + lensCurvature.hashCode()
    result = 31 * result + sharp.hashCode()
    result = 31 * result + tint.hashCode()
    return result
  }
}

@RequiresApi(33)
internal class LiquidNode(
  var liquid: Liquid?,
  var frost: Dp,
  var shape: Shape,
  var lensRefraction: Float,
  var lensCurvature: Float,
  var sharp: Float,
  var tint: Color,
) : Modifier.Node(),
  GlobalPositionAwareModifierNode,
  DrawModifierNode,
  CompositionLocalConsumerModifierNode,
  TraversableNode,
  ObserverModifierNode {

  companion object LiquidKey

  override val traverseKey: Any
    get() = LiquidKey

  private val shader = RuntimeShader(LiquidShader)
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
    super.onDetach()
  }

  private fun invalidateIfNeeded() {
    if (!isAttached) return

    val forbiddenSides = buildList {
      traverseAncestors(LiquefiableNode.LiquefiableKey) {
        (it as? LiquefiableNode)?.let { node -> add(node.liquefiable) }
        true
      }
    }

    // This has room for improvement, but it allows nodes to be both a content and effect node while preventing infinite draws if a
    // parent `liquefiable` node has child `liquid` nodes.
    val newSides = liquid?.liquefiables
      .orEmpty()
      .asSequence()
      .filterNot { it in forbiddenSides }
      .toList()

    val shouldInvalidate = newSides != liquefiables || bounds != lastBounds

    liquefiables = newSides
    lastBounds = bounds

    // Only invalidate if something actually changed
    if (shouldInvalidate) {
      invalidateDraw()
    }
  }

  override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
    if (!isAttached) return

    bounds = Rect(coordinates.positionOnScreen(), coordinates.size.toSize())
    invalidateIfNeeded()
  }

  override fun ContentDrawScope.draw() {
    if (liquefiables.isEmpty() || size.isEmpty()) {
      drawContent()
      return
    }

    val layer = cachedLayer?.takeUnless { it.isReleased }
      ?: currentValueOf(LocalGraphicsContext)
        .createGraphicsLayer()
        .also { cachedLayer = it }

    val density = currentValueOf(LocalDensity)
    val cornerRadius = shape.cornerRadiusPx(size, density)
    val blur = with(density) { frost.toPx() }

    shader.apply {
      setFloatUniform("blurRadius", blur)
      setFloatUniform("resolution", size.width, size.height)
      setFloatUniform("cornerRadius", cornerRadius)
      setFloatUniform("lensRefraction", lensRefraction)
      setFloatUniform("lensCurvature", lensCurvature)
      setFloatUniform("sharp", sharp)
      setColorUniform("tintColor", tint.toArgb())
    }

    val renderEffect = createRuntimeShaderEffect(shader, "content")

    layer.record {
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

    layer.clip = cornerRadius > 0f
    layer.renderEffect = renderEffect.asComposeRenderEffect()
    drawLayer(layer)
    drawContent()
  }

  override fun onObservedReadsChanged() = invalidateIfNeeded()
}
