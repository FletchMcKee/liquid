// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.dp

// These are Jetpack helper methods/classes, but they're internal so just adding them manually.
internal fun Modifier.elementOf(node: Modifier.Node): Modifier = this.then(ElementOf { node })

internal data class ElementOf<T : Modifier.Node>(val factory: () -> T) : ModifierNodeElement<T>() {
  override fun create(): T = factory()

  override fun update(node: T) {}

  override fun InspectorInfo.inspectableProperties() {
    name = "testNode"
  }
}

/**
 * Asserts that the expected color is present in this bitmap.
 *
 * @throws AssertionError if the expected color is not present.
 */
internal fun ImageBitmap.assertContainsColor(expectedColor: Color): ImageBitmap {
  if (!containsColor(expectedColor)) {
    throw AssertionError("The given color $expectedColor was not found in the bitmap.")
  }
  return this
}

internal fun ImageBitmap.assertDoesNotContainColor(unexpectedColor: Color): ImageBitmap {
  if (containsColor(unexpectedColor)) {
    throw AssertionError("The given color $unexpectedColor was found in the bitmap.")
  }
  return this
}

private fun ImageBitmap.containsColor(expectedColor: Color): Boolean {
  val pixels = this.toPixelMap()
  for (y in 0 until height) {
    for (x in 0 until width) {
      val color = pixels[x, y]
      if (color == expectedColor) {
        return true
      }
    }
  }
  return false
}

@Composable
internal fun SimpleLiquefiable(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
) = Box(
  modifier
    .size(50.dp)
    .background(Color.Red)
    .liquefiable(liquidState),
)

@Composable
internal fun Parent(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) = Box(modifier.size(200.dp)) {
  content()
}
