// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
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
  density: Density = Density(1f),
  content: @Composable () -> Unit,
) = CompositionLocalProvider(LocalDensity provides density) {
  Box(modifier.size(200.dp)) {
    content()
  }
}
