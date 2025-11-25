// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import assertk.all
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import kotlin.test.Test

class DrawUtilTest {
  @Test fun normalizedCornerRadii_circleShapeIs50Percent() {
    val result = CircleShape.normalizedCornerRadii(sizeHundred, densityOne)
    assertThat(result).all {
      hasSize(4)
      // This shouldn't have any precision issues.
      isEqualTo(floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f))
    }
  }

  @Test fun normalizedCornerRadii_rectangleShapeIs0Percent() {
    val result = RectangleShape.normalizedCornerRadii(sizeHundred, densityOne)
    assertThat(result).all {
      hasSize(4)
      // This shouldn't have any precision issues.
      isEqualTo(floatArrayOf(0f, 0f, 0f, 0f))
    }
  }

  @Test fun normalizedCornerRadii_roundedCornerShapeSub50() {
    val result = RoundedCornerShape(25).normalizedCornerRadii(sizeHundred, densityOne)
    assertThat(result).all {
      hasSize(4)
      // This shouldn't have any precision issues.
      isEqualTo(floatArrayOf(0.25f, 0.25f, 0.25f, 0.25f))
    }
  }

  @Test fun normalizedCornerRadii_densityCalculatedCorrectly() {
    val densityTwo = Density(2f)
    val sizeResult = RoundedCornerShape(20.dp).normalizedCornerRadii(sizeHundred, densityTwo)
    assertThat(sizeResult).all {
      hasSize(4)
      // 20.dp with 2f density results in 0.4f radii.
      isEqualTo(floatArrayOf(0.4f, 0.4f, 0.4f, 0.4f))
    }

    val dpSizeResult = RoundedCornerShape(20.dp).normalizedCornerRadii(
      size = with(densityTwo) { DpSize(width = 100.dp, height = 100.dp).toSize() },
      density = densityTwo,
    )
    assertThat(dpSizeResult).all {
      hasSize(4)
      // DpSize is adjusted with the same density.
      isEqualTo(floatArrayOf(0.2f, 0.2f, 0.2f, 0.2f))
    }
  }

  @Test fun normalizedCornerRadii_minDimensionIsRespected() {
    val portraitResult = RoundedCornerShape(25).normalizedCornerRadii(
      size = Size(width = 100f, height = 200f),
      density = densityOne,
    )
    assertThat(portraitResult).all {
      hasSize(4)
      // This shouldn't have any precision issues.
      isEqualTo(floatArrayOf(0.25f, 0.25f, 0.25f, 0.25f))
    }

    val landscapeResult = RoundedCornerShape(25).normalizedCornerRadii(
      size = Size(width = 200f, height = 100f),
      density = densityOne,
    )
    assertThat(landscapeResult).all {
      hasSize(4)
      // This shouldn't have any precision issues.
      isEqualTo(floatArrayOf(0.25f, 0.25f, 0.25f, 0.25f))
    }
  }

  @Test fun normalizedCornerRadii_clampsRadiiTo50Percent() {
    val shape = RoundedCornerShape(
      bottomEndPercent = 40,
      topEndPercent = 50,
      bottomStartPercent = 100,
      topStartPercent = 60,
    )

    val result = shape.normalizedCornerRadii(sizeHundred, densityOne)
    assertThat(result).hasSize(4)
    assertThat(result[0]).isEqualTo(0.4f)
    assertThat(result[1]).isEqualTo(0.5f)
    // Verify this one is clamped and not 0.625f.
    assertThat(result[2]).isEqualTo(0.5f)
    // scale = 1f / (bottomStart + topStart) = 0.00625
    // topStart = 60px * 0.00625 = 0.375
    assertThat(result[3]).isCloseTo(0.375f, Delta)
  }

  @Test fun normalizedCornerRadii_rtlIsSupported() {
    val shape = RoundedCornerShape(
      bottomEndPercent = 40,
      topEndPercent = 50,
      bottomStartPercent = 100,
      topStartPercent = 60,
    )

    val result = shape.normalizedCornerRadii(sizeHundred, densityOne, LayoutDirection.Rtl)
    assertThat(result).hasSize(4)
    // Verify this one is clamped
    assertThat(result[0]).isEqualTo(0.5f)
    // scale = 1f / (bottomStart + topStart) = 0.00625
    // topStart = 60px * 0.00625 = 0.375
    assertThat(result[1]).isCloseTo(0.375f, Delta)
    assertThat(result[2]).isEqualTo(0.4f)
    assertThat(result[3]).isEqualTo(0.5f)
  }

  @Suppress("ConstPropertyName")
  private companion object {
    const val Delta = 0.001f
    val sizeHundred = Size(width = 100f, height = 100f)
    val densityOne = Density(density = 1f)
  }
}
