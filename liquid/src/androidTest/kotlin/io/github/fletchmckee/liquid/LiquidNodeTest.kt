// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.github.fletchmckee.liquid.internal.LiquidNode
import kotlin.test.Test
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 33)
class LiquidNodeTest {
  @get:Rule val rule = createComposeRule()

  private lateinit var liquidState: LiquidState

  @Before fun setUp() {
    liquidState = LiquidState()
  }

  @Test fun defaultValuesObserved_whenNoScopeProvided() {
    var liquidBlockCount = 0
    val liquidNode = LiquidNode(liquidState) { liquidBlockCount++ }
    rule.apply {
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
        assertThat(scope.frost).isEqualTo(0.dp)
        assertThat(scope.shape).isEqualTo(RectangleShape)
        assertThat(scope.refraction).isEqualTo(0.25f)
        assertThat(scope.curve).isEqualTo(0.25f)
        assertThat(scope.edge).isEqualTo(0f)
      }
    }
  }

  @Test fun differingLiquidStates_whenLiquefiableBoundsChange_liquidNodeNotInvalidated() {
    var offset by mutableStateOf(IntOffset(0))
    var drawCount = 0
    var liquidBlockCount = 0
    // Own unique LiquidState
    val liquidNode = LiquidNode(LiquidState()) { liquidBlockCount++ }
    rule.apply {
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
  }

  @Test fun sharedLiquidStates_whenLiquefiableBoundsChange_liquidNodeInvalidated() {
    var showLiquefiable by mutableStateOf(true)
    var offset by mutableStateOf(IntOffset(0, 0))
    var drawCount = 0
    var liquidBlockCount = 0
    // Shared LiquidState
    val liquidNode = LiquidNode(liquidState) { liquidBlockCount++ }
    rule.apply {
      setContent {
        CompositionLocalProvider(LocalDensity provides Density(1f)) {
          Parent {
            if (showLiquefiable) {
              SimpleLiquefiable(liquidState, Modifier.offset { offset })
            }
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
        // onAttach and the Liquefiable with shared state being added.
        assertThat(liquidBlockCount).isEqualTo(2)
        assertThat(drawCount).isEqualTo(2)
        assertThat(
          liquidNode.reusableScope.liquefiables
            .single()
            .boundsOnScreen
            .topLeft,
        ).isEqualTo(Offset(0f, 0f))
      }
      runOnIdle { offset = IntOffset(10, 10) }
      runOnIdle {
        // The liquidBlock is only invalidated when liquefiables are added/removed. The draw pass observes the liquefiable's bounds and
        // graphicsLayer, so we should see the drawCount increment without the liquidBlockCount.
        assertThat(liquidBlockCount).isEqualTo(2)
        assertThat(drawCount).isEqualTo(3)
        // Verify that we do have the updated bounds.
        assertThat(
          liquidNode.reusableScope.liquefiables
            .single()
            .boundsOnScreen
            .topLeft,
        ).isEqualTo(Offset(10f, 10f))
      }
      // Verify same position does not cause new draws
      runOnIdle { offset = IntOffset(10, 10) }
      runOnIdle {
        assertThat(liquidBlockCount).isEqualTo(2)
        assertThat(drawCount).isEqualTo(3)
        assertThat(
          liquidNode.reusableScope.liquefiables
            .single()
            .boundsOnScreen
            .topLeft,
        ).isEqualTo(Offset(10f, 10f))
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
  }

  @Test fun liquidNode_reactsToFrostChanges() = runLiquidScopeTest(
    initialValue = 0.dp,
    changedValue = 10.dp,
    finalValue = 20.dp,
  ) { frost ->
    this.frost = frost
  }

  @Test fun liquidNode_reactsToShapeChanges() = runLiquidScopeTest(
    initialValue = RectangleShape,
    changedValue = RoundedCornerShape(10),
    finalValue = CircleShape,
  ) { shape ->
    this.shape = shape
  }

  @Test fun liquidNode_reactsToRefractionChanges() = runLiquidScopeTest(
    initialValue = 0.25f,
    changedValue = 0f,
    finalValue = 0.5f,
  ) { refraction ->
    this.refraction = refraction
  }

  @Test fun liquidNode_reactsToCurveChanges() = runLiquidScopeTest(
    initialValue = 0.25f,
    changedValue = 0f,
    finalValue = 0.5f,
  ) { curve ->
    this.curve = curve
  }

  @Test fun liquidNode_reactsToEdgeChanges() = runLiquidScopeTest(
    initialValue = 0f,
    changedValue = 0.1f,
    finalValue = 0.2f,
  ) { edge ->
    this.edge = edge
  }

  @Test fun liquidNode_notInBoundsOfLiquefiable_doesNotRenderLiquefiable() {
    val liquidNode = LiquidNode(liquidState) {}
    rule.apply {
      setContent {
        Parent {
          // LiquidNode is at 0x, 0y with 100w, 100h size. The LiquefiableNode is at 101x, 0y.
          SimpleLiquefiable(liquidState, Modifier.offset(101.dp))
          Box(
            Modifier
              .testTag("liquid")
              .semantics { testTagsAsResourceId = true }
              .size(100.dp)
              .elementOf(liquidNode),
          )
        }
      }

      runOnIdle {
        assertThat(
          liquidNode.reusableScope.paddedBounds()
            .overlaps(liquidState.liquefiables.single().boundsOnScreen),
        ).isFalse()
      }
      onNodeWithTag("liquid")
        .captureToImage()
        .assertDoesNotContainColor(Color.Red)
    }
  }

  @Test fun liquidNode_inBoundsOfLiquefiable_doesRenderLiquefiable() {
    val liquidNode = LiquidNode(liquidState) {}
    rule.apply {
      setContent {
        Parent {
          SimpleLiquefiable(liquidState)
          Box(
            Modifier
              .testTag("liquid")
              .semantics { testTagsAsResourceId = true }
              .size(100.dp)
              .elementOf(liquidNode),
          )
        }
      }

      runOnIdle {
        assertThat(
          liquidNode.reusableScope.paddedBounds()
            .overlaps(liquidState.liquefiables.single().boundsOnScreen),
        ).isTrue()
      }
      onNodeWithTag("liquid")
        .captureToImage()
        .assertContainsColor(Color.Red)
    }
  }

  private fun <T> runLiquidScopeTest(
    initialValue: T,
    changedValue: T,
    finalValue: T,
    onUpdate: LiquidScope.(T) -> Unit,
  ) {
    var property by mutableStateOf(initialValue)
    var drawCount = 0
    var liquidBlockCount = 0

    val liquidNode = LiquidNode(liquidState) {
      onUpdate(property)
      liquidBlockCount++
    }

    rule.apply {
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
}
