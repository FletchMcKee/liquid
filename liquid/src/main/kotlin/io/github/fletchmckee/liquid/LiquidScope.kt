// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public interface LiquidScope {
  /**
   * The blur radius applied behind the liquid effect, giving the appearance of frost. Useful when your [liquid] composable is expected to
   * display text as the liquid effects alone can diminish readability.
   *
   * NOTE: This is the most expensive property in this scope as it creates two separate shaders for creating the frost effect.
   *
   * Defaults to 0.dp. Negative values are ignored.
   */
  public var frost: Dp

  /**
   * The shape of the effect area, defining the clipping and outline of the effect.
   *
   * Defaults to [RectangleShape].
   */
  public var shape: Shape

  /**
   * Controls how much the background distorts through the lens. Setting this to 0 removes the liquid effect altogether, nullifying any
   * [curve] value.
   *
   * Defaults to 0.25f.
   */
  @setparam:FloatRange(from = 0.0)
  public var refraction: Float

  /**
   * Adjusts how strongly the lens curves at its center vs. edges. Setting this to 0 removes the liquid effect altogether, nullifying any
   * [refraction] value.
   *
   * Defaults to 0.25f.
   */
  @setparam:FloatRange(from = 0.0)
  public var curve: Float

  /**
   * Width of the feathered rim around the effect's edge.
   *
   * Higher values create a wider, softer edge and expand the region where rim lighting is applied.
   * Set to `0f` to disable this effect.
   *
   * Defaults to 0f.
   */
  @setparam:FloatRange(from = 0.0)
  public var edge: Float
}

// This is only used for API 32 and lower so that a provided shape is used.
internal class DefaultLiquidScope : LiquidScope {
  override var frost: Dp = 0.dp
  override var shape: Shape = RectangleShape
  override var refraction: Float = 0.25f
  override var curve: Float = 0.25f
  override var edge: Float = 0f

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as DefaultLiquidScope

    if (refraction != other.refraction) return false
    if (curve != other.curve) return false
    if (edge != other.edge) return false
    if (frost != other.frost) return false
    if (shape != other.shape) return false

    return true
  }

  override fun hashCode(): Int {
    var result = refraction.hashCode()
    result = 31 * result + curve.hashCode()
    result = 31 * result + edge.hashCode()
    result = 31 * result + frost.hashCode()
    result = 31 * result + shape.hashCode()
    return result
  }
}
