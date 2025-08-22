// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.internal.Bitmask
import io.github.fletchmckee.liquid.internal.Fields

public interface LiquidScope {
  public var frost: Dp
  public var shape: Shape
  public var refraction: Float
  public var curve: Float
  public var edge: Float
}

internal class LiquidScopeImpl : LiquidScope {
  internal var bitmask = Bitmask()

  override var frost: Dp = 0.dp
    set(value) {
      if (field != value) {
        bitmask += Fields.Frost
        field = value
      }
    }

  override var shape: Shape = RectangleShape
    set(value) {
      if (field != value) {
        bitmask += Fields.Shape
        field = value
      }
    }

  override var refraction: Float = 0f
    set(value) {
      if (field != value) {
        bitmask += Fields.Refraction
        field = value
      }
    }

  override var curve: Float = 0f
    set(value) {
      if (field != value) {
        bitmask += Fields.Curve
        field = value
      }
    }

  override var edge: Float = 0f
    set(value) {
      if (field != value) {
        bitmask += Fields.Edge
        field = value
      }
    }

  internal fun reset() {
    frost = 0.dp
    shape = RectangleShape
    refraction = 0f
    curve = 0f
    edge = 0f

    bitmask = Bitmask()
  }
}
