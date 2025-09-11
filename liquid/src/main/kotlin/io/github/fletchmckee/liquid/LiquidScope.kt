// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.annotation.FloatRange
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

public interface LiquidScope {
  /**
   * The blur radius applied behind the liquid effect, giving the appearance of frost.
   *
   * Useful when your [liquid] composable is expected to display text as the liquid effects alone can
   * diminish legibility.
   *
   * NOTE: This is the most expensive property in this scope as it creates two separate shaders for
   * rendering the frost effect.
   *
   * Defaults to 0.dp. No-op on API 30 and lower. Negative values are ignored.
   */
  public var frost: Dp

  /**
   * The shape of the effect area, defining the clipping and outline of the effect.
   *
   * It's recommended to use [CircleShape] or shapes with rounded corners for best liquid effects.
   *
   * Defaults to [CircleShape].
   */
  public var shape: Shape

  /**
   * Controls how much the background distorts through the lens. Setting this to 0 removes the liquid
   * effect altogether, nullifying any [curve] value.
   *
   * Defaults to 0.25f. No-op on API 32 and lower.
   */
  @setparam:FloatRange(from = 0.0)
  public var refraction: Float

  /**
   * Adjusts how strongly the lens curves at its center vs. edges. Setting this to 0 removes the liquid
   * effect altogether, nullifying any [refraction] value.
   *
   * Defaults to 0.25f. No-op on API 32 and lower.
   */
  @setparam:FloatRange(from = 0.0)
  public var curve: Float

  /**
   * Width of the rim lighting around the effect's edge.
   *
   * Higher values create a wider, softer edge and expand the region where rim lighting is applied.
   * Set to `0f` to disable this effect.
   *
   * Defaults to 0f. On API 32 and lower, this becomes a boolean where a value > 0f draws a similar effect, and 0f removes it.
   */
  @setparam:FloatRange(from = 0.0)
  public var edge: Float

  /**
   * Optional tint color applied to the liquid effect.
   *
   * This is mainly a convenience property if you want the effect to carry a background color without
   * needing to wrap it in a separate call to [androidx.compose.foundation.background].
   *
   * NOTE: If the alpha of the provided color is 1.0, the liquid effect will be nullified with only
   * the edge lighting being rendered if provided.
   *
   * Defaults to [Color.Unspecified]
   */
  public var tint: Color
}
