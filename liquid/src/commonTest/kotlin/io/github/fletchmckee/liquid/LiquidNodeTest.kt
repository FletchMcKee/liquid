// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTestApi::class)
@file:Suppress("VisibleForTests") // Not sure why this isn't considered a test class

package io.github.fletchmckee.liquid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isZero
import io.github.fletchmckee.liquid.internal.AbstractLiquidNode
import kotlin.test.BeforeTest
import kotlin.test.Test

class LiquidNodeTest {
  private lateinit var liquidState: LiquidState

  @BeforeTest fun setUp() {
    liquidState = LiquidState()
  }

  @Test fun defaultValuesObserved_whenNoScopeProvided() = runComposeUiTest {
    var liquidBlockCount = 0
    val liquidNode = LiquidNode(liquidState) { liquidBlockCount++ }
    setContent {
      Parent {
        Box(
          Modifier
            .size(100.dp)
            .elementOf(liquidNode),
        )
      }
    }

    runOnIdle {
      val scope = liquidNode.reusableScope
      assertThat(liquidBlockCount).isEqualTo(1)
      assertThat(scope.refraction).isEqualTo(0.25f)
      assertThat(scope.curve).isEqualTo(0.25f)
      assertThat(scope.edge).isZero()
      assertThat(scope.tint).isEqualTo(Color.Unspecified)
      assertThat(scope.saturation).isEqualTo(1f)
      assertThat(scope.dispersion).isZero()
      assertThat(scope.frost).isEqualTo(0.dp)
      assertThat(scope.shape).isEqualTo(CircleShape)
    }
  }

  @Test fun differingLiquidStates_liquefiableBoundsChange_liquidNodeNotInvalidated() = runComposeUiTest {
    var offset by mutableStateOf(IntOffset(0))
    var drawCount = 0
    var liquidBlockCount = 0
    // Own unique LiquidState
    val liquidNode = LiquidNode(LiquidState()) { liquidBlockCount++ }
    setContent {
      Parent {
        SimpleLiquefiable(liquidState, Modifier.offset { offset })
        Box(
          Modifier
            .size(100.dp)
            .elementOf(liquidNode)
            .drawBehind { drawCount++ },
        )
      }
    }

    runOnIdle {
      // onAttach
      assertThat(liquidBlockCount).isEqualTo(1)
      assertThat(drawCount).isEqualTo(1)
    }
    // Changing the unrelated liquefiable's offset should not change our liquidNode.
    runOnIdle { offset = IntOffset(10) }
    runOnIdle {
      // Remains unchanged.
      assertThat(liquidBlockCount).isEqualTo(1)
      assertThat(drawCount).isEqualTo(1)
    }
  }

  @Test fun sharedLiquidStates_liquefiableBoundsChange_liquidNodeInvalidated() = runComposeUiTest {
    var showLiquefiable by mutableStateOf(true)
    var offset by mutableStateOf(IntOffset(0, 0))
    var drawCount = 0
    var liquidBlockCount = 0
    var coords: LayoutCoordinates? = null
    // Shared LiquidState
    val liquidNode = LiquidNode(liquidState) { liquidBlockCount++ }
    setContent {
      Parent {
        if (showLiquefiable) {
          SimpleLiquefiable(
            liquidState,
            Modifier
              .offset { offset }
              .onGloballyPositioned { coords = it },
          )
        }
        Box(
          Modifier
            .size(100.dp)
            .elementOf(liquidNode)
            .drawBehind { drawCount++ },
        )
      }
    }

    runOnIdle {
      // onAttach and the Liquefiable being added.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
      val expectedBounds = Rect(coords!!.positionOnScreen(), coords.size.toSize())
      assertThat(
        liquidNode.reusableScope.liquefiables
          .single()
          .boundsOnScreen,
      ).isEqualTo(expectedBounds)
    }
    runOnIdle { offset = IntOffset(10, 10) }
    runOnIdle {
      // The liquidBlock is only invalidated when liquefiables are added/removed. The draw pass observes the liquefiable's bounds and
      // graphicsLayer, so we should see the drawCount increment without the liquidBlockCount.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(3)
      // Verify that we do have the updated bounds.
      val expectedBounds = Rect(coords!!.positionOnScreen(), coords.size.toSize())
      assertThat(
        liquidNode.reusableScope.liquefiables
          .single()
          .boundsOnScreen,
      ).isEqualTo(expectedBounds)
    }
    // Verify same position doesn't cause new draws
    runOnIdle { offset = IntOffset(10, 10) }
    runOnIdle {
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(3)
      val expectedBounds = Rect(coords!!.positionOnScreen(), coords.size.toSize())
      assertThat(
        liquidNode.reusableScope.liquefiables
          .single()
          .boundsOnScreen,
      ).isEqualTo(expectedBounds)
      assertThat(liquidState.liquefiables.size).isEqualTo(1)
    }
    // Verify removal of the liquefiable.
    runOnIdle { showLiquefiable = false }
    runOnIdle {
      // Removal does invalidate the liquidBlock.
      assertThat(liquidBlockCount).isEqualTo(3)
      assertThat(drawCount).isEqualTo(4)
      assertThat(liquidNode.reusableScope.liquefiables).isEmpty()
      assertThat(liquidState.liquefiables).isEmpty()
    }
  }

  @Test fun removedLiquidNode_liquefiableBoundsChange_notInvalidated() = runComposeUiTest {
    var showLiquid by mutableStateOf(true)
    var offset by mutableStateOf(IntOffset(0, 0))
    var drawCount = 0
    var liquidBlockCount = 0
    val liquidNode = LiquidNode(liquidState) { liquidBlockCount++ }
    setContent {
      Parent {
        SimpleLiquefiable(
          liquidState,
          Modifier.offset { offset },
        )
        if (showLiquid) {
          Box(
            Modifier
              .size(100.dp)
              .elementOf(liquidNode)
              .drawBehind { drawCount++ },
          )
        }
      }
    }

    runOnIdle {
      // onAttach and the Liquefiable being added.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
    }
    runOnIdle { showLiquid = false }
    runOnIdle {
      // Verify no draws/invalidations occurred.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
    }
    runOnIdle { offset = IntOffset(10, 10) }
    runOnIdle {
      // Verify the liquidNode is no longer observing liquefiable changes.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
    }
    runOnIdle { showLiquid = true }
    runOnIdle {
      // Verify incremented draw/liquidBlockCounts and the same liquefiable is now observed.
      assertThat(liquidBlockCount).isEqualTo(3)
      assertThat(drawCount).isEqualTo(3)
      assertThat(liquidNode.reusableScope.liquefiables.single())
        .isEqualTo(liquidState.liquefiables.single())
    }
  }

  @Test fun removedLiquidNode_liquidScopeParameterChange_notInvalidated() = runComposeUiTest {
    var showLiquid by mutableStateOf(true)
    var curve by mutableFloatStateOf(0.25f)
    var drawCount = 0
    var liquidBlockCount = 0
    val liquidNode = LiquidNode(liquidState) {
      this.curve = curve
      liquidBlockCount++
    }
    setContent {
      Parent {
        SimpleLiquefiable(liquidState)
        if (showLiquid) {
          Box(
            Modifier
              .size(100.dp)
              .elementOf(liquidNode)
              .drawBehind { drawCount++ },
          )
        }
      }
    }

    runOnIdle {
      // onAttach and Liquefiable being added.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
    }
    runOnIdle { showLiquid = false }
    runOnIdle {
      // Verify no draws/invalidations occurred.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
    }
    runOnIdle { curve = 0.5f }
    runOnIdle {
      // Verify the liquidNode is no longer observing its own parameter changes.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
    }
    runOnIdle { showLiquid = true }
    runOnIdle {
      // Verify incremented draw/liquidBlockCounts and the curve matches the updated value.
      assertThat(liquidBlockCount).isEqualTo(3)
      assertThat(drawCount).isEqualTo(3)
      assertThat(liquidNode.reusableScope.liquefiables.single())
        .isEqualTo(liquidState.liquefiables.single())
      assertThat(liquidNode.reusableScope.curve).isEqualTo(0.5f)
    }
  }

  @Test fun liquidNodeDrawPasses_reactToArgbChanges_notTintChanges() = runComposeUiTest {
    var tint by mutableStateOf(Color.Unspecified)
    var drawCount = 0
    var liquidBlockCount = 0
    val liquidNode = LiquidNode(liquidState) {
      this.tint = tint
      liquidBlockCount++
    }
    setContent {
      Parent {
        SimpleLiquefiable(liquidState)
        Box(
          Modifier
            .size(100.dp)
            .elementOf(liquidNode)
            .drawBehind { drawCount++ },
        )
      }
    }

    runOnIdle {
      // onAttach and Liquefiable being added.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
    }
    // Different tint but same argb value.
    runOnIdle { tint = Color.Transparent }
    runOnIdle {
      // The liquidBlockCount will increment as we did provide a different tint.
      assertThat(liquidBlockCount).isEqualTo(3)
      // But it should not `invalidateDraw`
      assertThat(drawCount).isEqualTo(2)
    }
    runOnIdle { tint = Color.Red }
    runOnIdle {
      // Now we should have incremented draw/liquidBlockCounts
      assertThat(liquidBlockCount).isEqualTo(4)
      assertThat(drawCount).isEqualTo(3)
    }
  }

  @Test fun nearestAncestorLiquefiable_filteredOutOfLiquidNodeLiquefiables() = runComposeUiTest {
    var drawCount = 0
    var liquidBlockCount = 0
    val liquidNode = LiquidNode(liquidState) { liquidBlockCount++ }
    setContent {
      Parent {
        SimpleLiquefiable(liquidState, Modifier.size(50.dp))
        Box(
          Modifier
            .size(100.dp)
            .liquefiable(liquidState)
            .elementOf(liquidNode)
            .drawBehind { drawCount++ },
        )
      }
    }

    runOnIdle {
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
      // The state will still contain the nearest ancestor, but the liquidNode's reusableScope should not.
      assertThat(liquidState.liquefiables.size).isEqualTo(2)
      assertThat(liquidNode.reusableScope.liquefiables.size).isEqualTo(1)
      // Verify the correct liquefiable remains. The nearest ancestor that was filtered would have Size(100f, 100f).
      assertThat(
        liquidNode.reusableScope.liquefiables
          .single()
          .boundsOnScreen
          .size,
      ).isEqualTo(Size(50f, 50f))
    }
  }

  @Test fun parentInvalidations_unaffectedByLiquidScopeParameterChanges() = runComposeUiTest {
    var refraction by mutableFloatStateOf(0.25f)
    var parentCompositionCount = 0
    var drawCount = 0
    var liquidBlockCount = 0
    val liquidNode = LiquidNode(liquidState) {
      this.refraction = refraction
      liquidBlockCount++
    }
    setContent {
      Parent {
        parentCompositionCount++
        SimpleLiquefiable(liquidState)
        Box(
          Modifier
            .size(100.dp)
            .elementOf(liquidNode)
            .drawBehind { drawCount++ },
        )
      }
    }

    runOnIdle {
      // onAttach and Liquefiable being added.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
      assertThat(parentCompositionCount).isEqualTo(1)
    }
    runOnIdle { refraction = 0.5f }
    runOnIdle {
      // Verify incremented draw/liquidBlockCounts, but the parentCompositionCount should remain at one
      // since the refraction state read is deferred to our LiquidScope block.
      assertThat(liquidBlockCount).isEqualTo(3)
      assertThat(drawCount).isEqualTo(3)
      assertThat(parentCompositionCount).isEqualTo(1)
    }
  }

  @Test fun liquidNode_detectsRotations() = runComposeUiTest {
    var rotation by mutableFloatStateOf(0f)
    var drawCount = 0
    var liquidBlockCount = 0
    var coords: LayoutCoordinates? = null
    // Own unique LiquidState
    val liquidNode = LiquidNode(liquidState) { liquidBlockCount++ }
    setContent {
      Parent {
        SimpleLiquefiable(liquidState)
        Box(
          Modifier
            .size(100.dp)
            .graphicsLayer { rotationZ = rotation }
            .elementOf(liquidNode)
            .drawBehind { drawCount++ }
            .onGloballyPositioned { coords = it },
        )
      }
    }

    runOnIdle {
      // onAttach and Liquefiable being added.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
      assertThat(coords!!.boundsInRoot())
        .isEqualTo(liquidNode.reusableScope.boundsInRoot)
    }

    runOnIdle { rotation = 45f }
    runOnIdle {
      // The block shouldn't invalidate, but the draw should.
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(3)
      assertThat(coords!!.boundsInRoot())
        .isEqualTo(liquidNode.reusableScope.boundsInRoot)
    }
  }

  @Test fun liquidNode_reactsToFrostChanges() = runLiquidScopeTest(
    initialValue = 0.dp,
    changedValue = 10.dp,
    finalValue = 20.dp,
    onUpdate = { frost = it },
  )

  @Test fun liquidNode_reactsToShapeChanges() = runLiquidScopeTest(
    initialValue = CircleShape,
    changedValue = RoundedCornerShape(10),
    finalValue = RectangleShape,
    onUpdate = { shape = it },
  )

  @Test fun liquidNode_reactsToRefractionChanges() = runLiquidScopeTest(
    initialValue = 0.25f,
    changedValue = 0f,
    finalValue = 0.5f,
    onUpdate = { refraction = it },
  )

  @Test fun liquidNode_reactsToCurveChanges() = runLiquidScopeTest(
    initialValue = 0.25f,
    changedValue = 0f,
    finalValue = 0.5f,
    onUpdate = { curve = it },
  )

  @Test fun liquidNode_reactsToEdgeChanges() = runLiquidScopeTest(
    initialValue = 0f,
    changedValue = 0.1f,
    finalValue = 0.2f,
    onUpdate = { edge = it },
  )

  @Test fun liquidNode_reactsToTintChanges() = runLiquidScopeTest(
    initialValue = Color.Unspecified,
    changedValue = Color.Green,
    finalValue = Color.Blue,
    onUpdate = { tint = it },
  )

  @Test fun liquidNode_reactsToSaturationChanges() = runLiquidScopeTest(
    initialValue = 1.0f,
    changedValue = 1.5f,
    finalValue = 0.5f,
    onUpdate = { saturation = it },
  )

  @Test fun liquidNode_reactsToDispersionChanges() = runLiquidScopeTest(
    initialValue = 0f,
    changedValue = 0.5f,
    finalValue = 1f,
    onUpdate = { dispersion = it },
  )

  private fun <T> runLiquidScopeTest(
    initialValue: T,
    changedValue: T,
    finalValue: T,
    onUpdate: LiquidScope.(T) -> Unit,
  ) = runComposeUiTest {
    var property by mutableStateOf(initialValue)
    var drawCount = 0
    var liquidBlockCount = 0

    val liquidNode = LiquidNode(liquidState) {
      onUpdate(property)
      liquidBlockCount++
    }

    setContent {
      Parent {
        SimpleLiquefiable(liquidState)
        Box(
          Modifier
            .size(100.dp)
            .elementOf(liquidNode)
            .drawBehind { drawCount++ },
        )
      }
    }

    // Verify initial counts.
    runOnIdle {
      // onAttach and liquefiable added
      assertThat(liquidBlockCount).isEqualTo(2)
      assertThat(drawCount).isEqualTo(2)
    }
    runOnIdle { property = changedValue }
    runOnIdle {
      // Should increment with the changeValue
      assertThat(liquidBlockCount).isEqualTo(3)
      assertThat(drawCount).isEqualTo(3)
    }

    // Verify no changes when using the same value.
    runOnIdle { property = changedValue }
    runOnIdle {
      assertThat(liquidBlockCount).isEqualTo(3)
      assertThat(drawCount).isEqualTo(3)
    }

    // Verify one last change to reduce false positives.
    runOnIdle { property = finalValue }
    runOnIdle {
      assertThat(liquidBlockCount).isEqualTo(4)
      assertThat(drawCount).isEqualTo(4)
    }
  }
}

private class LiquidNode(
  liquidState: LiquidState,
  block: LiquidScope.() -> Unit,
) : AbstractLiquidNode(liquidState, block) {
  override fun createRenderEffect(): RenderEffect? = null
}
