// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotZero
import assertk.assertions.isZero
import io.github.fletchmckee.liquid.internal.Fields
import io.github.fletchmckee.liquid.internal.LiquidScopeImpl
import kotlin.test.BeforeTest
import kotlin.test.Test

class LiquidScopeTest {
  private lateinit var scope: LiquidScopeImpl

  @BeforeTest fun setup() {
    scope = LiquidScopeImpl()
  }

  @Test fun initialValues_areCorrect() {
    assertThat(scope.frost).isEqualTo(0.dp)
    assertThat(scope.shape).isEqualTo(CircleShape)
    assertThat(scope.refraction).isEqualTo(0.25f)
    assertThat(scope.curve).isEqualTo(0.25f)
    assertThat(scope.edge).isZero()
    assertThat(scope.tint).isEqualTo(Color.Unspecified)
    assertThat(scope.saturation).isEqualTo(1f)
    assertThat(scope.dispersion).isZero()
    assertThat(scope.argbColor).isZero()
    assertThat(scope.size).isEqualTo(Size.Unspecified)
    assertThat(scope.positionOnScreen).isEqualTo(Offset.Unspecified)
    assertThat(scope.inverseScaleX).isEqualTo(1f)
    assertThat(scope.inverseScaleY).isEqualTo(1f)
    assertThat(scope.inverseRotationZ).isZero()
    assertThat(scope.boundsInRoot).isEqualTo(Rect.Zero)
    assertThat(scope.liquefiables).isEmpty()
    assertThat(scope.mutatedFields).isZero()
  }

  @Test fun reset_cleansMutatedFields() {
    scope.setNonDefaultValues()
    assertThat(scope.mutatedFields).isNotZero()
    scope.clean()
    assertThat(scope.mutatedFields).isZero()
  }

  @Test fun frostMutationsObserved() {
    scope.frost = 10.dp
    assertThat(scope.frostRadius).isEqualTo(10f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Frost)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun shapeMutationsObserved_whenSizeIsSpecified() {
    // We don't set the shape flag unless we have a specified size.
    scope.size = Size(width = 50f, height = 50f)
    scope.shape = RoundedCornerShape(5)
    assertThat(scope.shape).isEqualTo(RoundedCornerShape(5))
    assertThat(scope.cornerRadii).isEqualTo(floatArrayOf(0.05f, 0.05f, 0.05f, 0.05f))
    // Verify both Size and Shape bits are set.
    assertThat(scope.mutatedFields and Fields.Size).isEqualTo(Fields.Size)
    assertThat(scope.mutatedFields and Fields.Shape).isEqualTo(Fields.Shape)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun shapeMutationsNotObserved_whenSizeIsUnspecified() {
    // Size is unspecified by default, so while the shape is set, the cornerRadii will not be set.
    scope.shape = RoundedCornerShape(5)
    assertThat(scope.shape).isEqualTo(RoundedCornerShape(5))
    assertThat(scope.cornerRadii).isEqualTo(floatArrayOf(0f, 0f, 0f, 0f))
    // Neither size nor shape should be set.
    assertThat(scope.mutatedFields and Fields.Size).isZero()
    assertThat(scope.mutatedFields and Fields.Shape).isZero()
    // RenderEffect and InvalidateFlags should not be set as size is unspecified.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isZero()
  }

  @Test fun refractionMutationsObserved() {
    scope.refraction = 0.5f
    assertThat(scope.refraction).isEqualTo(0.5f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Refraction)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun curveMutationsObserved() {
    scope.curve = 0.5f
    assertThat(scope.curve).isEqualTo(0.5f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Curve)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun edgeMutationsObserved() {
    scope.edge = 0.5f
    assertThat(scope.edge).isEqualTo(0.5f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Edge)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun tintMutationsObserved() {
    scope.tint = Color.Red
    assertThat(scope.tint).isEqualTo(Color.Red)
    assertThat(scope.argbColor).isEqualTo(-65536)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Tint)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun saturationMutationsObserved() {
    scope.saturation = 1.5f
    assertThat(scope.saturation).isEqualTo(1.5f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Saturation)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun dispersionMutationsObserved() {
    scope.dispersion = 0.5f
    assertThat(scope.dispersion).isEqualTo(0.5f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Dispersion)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun differentTints_withSameArgbValue_doNotInvalidate() {
    scope.tint = Color.Transparent
    assertThat(scope.tint).isEqualTo(Color.Transparent)
    assertThat(scope.argbColor).isZero()
    assertThat(scope.mutatedFields).isZero()
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isZero()
  }

  @Test fun sizeMutationsObserved() {
    scope.size = Size(width = 50f, height = 50f)
    assertThat(scope.size).isEqualTo(Size(width = 50f, height = 50f))
    // Changing size also changes the cornerRadii since we have CircleShape as the default.
    assertThat(scope.cornerRadii).isEqualTo(floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f))
    assertThat(scope.mutatedFields and Fields.Size).isEqualTo(Fields.Size)
    assertThat(scope.mutatedFields and Fields.Shape).isEqualTo(Fields.Shape)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun positionOnScreenMutationsObserved() {
    scope.positionOnScreen = Offset(x = 50f, y = 50f)
    assertThat(scope.positionOnScreen).isEqualTo(Offset(x = 50f, y = 50f))
    assertThat(scope.mutatedFields).isEqualTo(Fields.PositionOnScreen)
    // Verify the RenderEffect is not flagged.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isZero()
    // However the InvalidateFlags should be flagged.
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun liquefiablesMutationsObserved() {
    val liquefiable = Liquefiable()
    scope.liquefiables = listOf(liquefiable)
    assertThat(scope.liquefiables.single()).isEqualTo(liquefiable)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Liquefiables)
    // Verify the RenderEffect is not flagged.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isZero()
    // However the InvalidateFlags should be flagged.
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  @Test fun densityMutationsUpdateFrostRadius() {
    // The frost property is the only public API Dp property we expose. Other density dependent values
    // like size and cornerRadii will be updated when onGloballyPositioned is triggered.
    scope.frost = 10.dp
    assertThat(scope.frost).isEqualTo(10.dp)
    assertThat(scope.frostRadius).isEqualTo(10f)
    assertThat(scope.mutatedFields).isEqualTo(Fields.Frost)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
    scope.clean() // Clean the tracker.

    scope.density = Density(3f)
    assertThat(scope.frost).isEqualTo(10.dp) // This should remain 10.dp.
    assertThat(scope.frostRadius).isEqualTo(30f) // This should change.
    assertThat(scope.mutatedFields).isEqualTo(Fields.Frost)
    // Verify the RenderEffect and InvalidateFlags are not 0.
    assertThat(scope.mutatedFields and Fields.RenderEffectFields).isNotZero()
    assertThat(scope.mutatedFields and Fields.InvalidateFlags).isNotZero()
  }

  private fun LiquidScope.setNonDefaultValues() {
    refraction = 0.5f
    curve = 0.5f
    edge = 0.1f
    tint = Color.Red
    saturation = 1.5f
    frost = 10.dp
    shape = CircleShape
  }
}
