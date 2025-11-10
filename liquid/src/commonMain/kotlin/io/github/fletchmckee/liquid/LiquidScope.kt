// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.annotation.FloatRange
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * Defines the configuration and visual properties available within the [liquid] effect scope,
 * enabling distortion of the sampled content.
 */
public interface LiquidScope {
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
   * **Note:** If the alpha of the provided color is 1.0, the liquid effect will be nullified with only
   * the edge lighting being rendered if provided.
   *
   * Defaults to [Color.Unspecified]
   */
  public var tint: Color

  /**
   * Adjusts the color saturation of the content behind the liquid effect.
   *
   * Values greater than 1f create more vivid colors, while values less than 1f
   * decrease create more muted colors. A value of 0f results in grayscale.
   *
   * Defaults to 1f (no saturation change).
   */
  @setparam:FloatRange(from = 0.0)
  public var saturation: Float

  /**
   * Controls the chromatic aberration effect, which separates RGB channels to simulate
   * light dispersion through a lens.
   *
   * Higher values create more pronounced color separation, similar to light passing through
   * a prism. Set to 0f to disable chromatic aberration.
   *
   * Defaults to 0f. No-op on API 32 and lower. Negative values are ignored.
   */
  @setparam:FloatRange(from = 0.0)
  public var dispersion: Float

  /**
   * The blur radius applied behind the liquid effect, giving the appearance of frost.
   *
   * Useful when your [liquid] composable is expected to display text as the liquid effects alone can
   * diminish legibility.
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
}
