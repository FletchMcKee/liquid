// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isZero
import io.github.fletchmckee.liquid.internal.Fields
import io.github.fletchmckee.liquid.internal.LiquidScopeImpl
import org.junit.Before
import org.junit.Test

class LiquidScopeTest {
  private lateinit var scope: LiquidScopeImpl

  @Before fun setup() {
    scope = LiquidScopeImpl()
  }

  @Test fun `initial values are correct`() {
    assertThat(scope.frost).isEqualTo(0.dp)
    assertThat(scope.shape).isEqualTo(RectangleShape)
    assertThat(scope.refraction).isEqualTo(0.25f)
    assertThat(scope.curve).isEqualTo(0.25f)
    assertThat(scope.edge).isZero()
    assertThat(scope.tint).isEqualTo(Color.Unspecified)
    assertThat(scope.argbColor).isZero()
    assertThat(scope.size).isEqualTo(Size.Unspecified)
    assertThat(scope.positionOnScreen).isEqualTo(Offset.Zero)
    assertThat(scope.liquefiables).isEmpty()
    assertThat(scope.mutatedFields).isZero()
  }

  @Test fun `reset values are correct`() {
    scope.setNonDefaultValues()
    scope.reset()
    assertThat(scope.frost).isEqualTo(0.dp)
    assertThat(scope.shape).isEqualTo(RectangleShape)
    // We have to reset to the default values. Otherwise if users rely on the default values and
    // use a LazyList where items are detached and re-attached frequently, the effect would disappear.
    assertThat(scope.refraction).isEqualTo(0.25f)
    assertThat(scope.curve).isEqualTo(0.25f)
    assertThat(scope.edge).isZero()
    assertThat(scope.tint).isEqualTo(Color.Unspecified)
    assertThat(scope.argbColor).isZero()
    assertThat(scope.size).isEqualTo(Size.Unspecified)
    assertThat(scope.positionOnScreen).isEqualTo(Offset.Zero)
    assertThat(scope.liquefiables).isEmpty()
    assertThat(scope.mutatedFields).isZero()
  }

  @Test fun `frost mutations observed`() {
    scope.frost = 10.dp
    assertThat(scope.frost).isEqualTo(10.dp)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Frost)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotEqualTo(0)
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `shape mutations observed`() {
    scope.shape = CircleShape
    assertThat(scope.shape).isEqualTo(CircleShape)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Shape)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotEqualTo(0)
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `refraction mutations observed`() {
    scope.refraction = 0.5f
    assertThat(scope.refraction).isEqualTo(0.5f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Refraction)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotEqualTo(0)
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `curve mutations observed`() {
    scope.curve = 0.5f
    assertThat(scope.curve).isEqualTo(0.5f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Curve)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotEqualTo(0)
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `edge mutations observed`() {
    scope.edge = 0.5f
    assertThat(scope.edge).isEqualTo(0.5f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Edge)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotEqualTo(0)
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `tint mutations observed`() {
    scope.tint = Color.Red
    assertThat(scope.tint).isEqualTo(Color.Red)
    assertThat(scope.argbColor).isEqualTo(-65536)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Tint)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotEqualTo(0)
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `different tints with same argb value do not invalidate`() {
    scope.tint = Color.Transparent
    assertThat(scope.tint).isEqualTo(Color.Transparent)
    assertThat(scope.argbColor).isZero()
    assertThat(scope.mutatedFields).isZero()
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isZero()
  }

  @Test fun `size mutations observed`() {
    scope.size = Size(width = 50f, height = 50f)
    assertThat(scope.size).isEqualTo(Size(width = 50f, height = 50f))
    assertThat(scope.mutatedFields).isEqualTo(Fields.Size)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotEqualTo(0)
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `positionOnScreen mutations observed`() {
    scope.positionOnScreen = Offset(x = 50f, y = 50f)
    assertThat(scope.positionOnScreen).isEqualTo(Offset(x = 50f, y = 50f))
    assertThat(scope.mutatedFields).isEqualTo(Fields.PositionOnScreen)
    // Verify the RenderEffect is not flagged.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isZero()
    // However the InvalidateFlags should be flagged.
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `liquefiables mutations observed`() {
    val liquefiable = Liquefiable()
    scope.liquefiables = listOf(liquefiable)
    assertThat(scope.liquefiables.single()).isEqualTo(liquefiable)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Liquefiables)
    // Verify the RenderEffect is not flagged.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isZero()
    // However the InvalidateFlags should be flagged.
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotEqualTo(0)
  }

  @Test fun `paddedBounds pads correctly`() {
    // First verify we can handle unspecified Size correctly
    val unspecifiedBounds = scope.paddedBounds()
    assertThat(unspecifiedBounds).isEqualTo(Rect.Zero)

    scope.size = Size(width = 50f, height = 50f)
    val noFrostBounds = scope.paddedBounds()
    assertThat(noFrostBounds).isEqualTo(
      Rect(
        left = 0f,
        top = 0f,
        right = 50f,
        bottom = 50f,
      ),
    )

    scope.positionOnScreen = Offset(x = 10f, y = 5f)
    val noFrostWithOffsetBounds = scope.paddedBounds()
    assertThat(noFrostWithOffsetBounds).isEqualTo(
      Rect(
        left = 10f,
        top = 5f,
        right = 60f, // x + width
        bottom = 55f, // y + width
      ),
    )

    val frostWithOffsetBounds = scope.paddedBounds(padding = 10f)
    assertThat(frostWithOffsetBounds).isEqualTo(
      Rect(
        left = 0f, // x - padding
        top = -5f, // y - padding
        right = 70f, // x + width + padding
        bottom = 65f, // y + width + padding
      ),
    )
  }

  private fun LiquidScope.setNonDefaultValues() {
    frost = 10.dp
    shape = CircleShape
    refraction = 0.5f
    curve = 0.5f
    edge = 0.1f
    tint = Color.Red
  }
}
