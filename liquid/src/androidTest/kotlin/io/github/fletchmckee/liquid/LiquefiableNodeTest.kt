// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isSameInstanceAs
import io.github.fletchmckee.liquid.internal.LiquefiableNode
import kotlin.test.Test
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 31)
class LiquefiableNodeTest {
  @get:Rule val rule = createComposeRule()

  private lateinit var liquidState: LiquidState

  @Before fun setUp() {
    liquidState = LiquidState()
  }

  @Test fun removingAndAddingSameLiquefiable_doesNotCreateLiquidStateDuplicates() {
    val liquefiableNode = LiquefiableNode(liquidState)
    var showLiquefiable by mutableStateOf(true)
    var drawCount = 0
    rule.apply {
      setContent {
        Parent {
          if (showLiquefiable) {
            Box(Modifier.size(50.dp).elementOf(liquefiableNode))
          }
          Box(
            Modifier
              .size(100.dp)
              .liquid(liquidState)
              .drawBehind { drawCount++ },
          )
        }
      }

      lateinit var graphicsLayer: GraphicsLayer
      runOnIdle {
        assertThat(liquidState.liquefiables.size).isEqualTo(1)
        graphicsLayer = liquefiableNode.liquefiable.layer!!
        showLiquefiable = false
      }
      runOnIdle {
        assertThat(liquidState.liquefiables).isEmpty()
        assertThat(liquefiableNode.liquefiable.layer).isNull()
        assertThat(liquefiableNode.liquefiable.boundsOnScreen).isEqualTo(Rect.Zero)
        showLiquefiable = true
      }
      runOnIdle {
        assertThat(liquidState.liquefiables.size).isEqualTo(1)
        assertThat(liquefiableNode.liquefiable.layer).isNotEqualTo(graphicsLayer)
      }
    }
  }

  @Test fun graphicsLayerRemainsUnchanged_boundsOnScreenUpdates() {
    val liquefiableNode = LiquefiableNode(liquidState)
    var offset by mutableStateOf(IntOffset.Zero)
    var coords: LayoutCoordinates? = null
    rule.apply {
      setContent {
        CompositionLocalProvider(LocalDensity provides Density(1f)) {
          Parent {
            Box(
              Modifier
                .size(50.dp)
                .offset { offset }
                .elementOf(liquefiableNode)
                .onGloballyPositioned { coords = it },
            )
          }
        }
      }

      lateinit var graphicsLayer: GraphicsLayer
      runOnIdle {
        assertThat(liquidState.liquefiables.size).isEqualTo(1)
        val expectedBounds = Rect(coords!!.positionOnScreen(), coords.size.toSize())
        assertThat(liquefiableNode.liquefiable.boundsOnScreen)
          .isEqualTo(expectedBounds)
        graphicsLayer = liquefiableNode.liquefiable.layer!!
      }
      runOnIdle { offset = IntOffset(10, 10) }
      runOnIdle {
        assertThat(liquidState.liquefiables.size).isEqualTo(1)
        // Verify the graphicsLayer remains unchanged despite the offset update.
        assertThat(liquefiableNode.liquefiable.layer).isSameInstanceAs(graphicsLayer)
      }
    }
  }

  @Test fun emptySizeLiquefiable_doesNotRecordContent() {
    var size by mutableStateOf(0.dp)
    val liquefiableNode = LiquefiableNode(liquidState)
    rule.apply {
      setContent {
        Parent {
          Box(
            Modifier
              .size(size)
              .elementOf(liquefiableNode),
          )
        }
      }

      runOnIdle {
        // A null graphicsLayer indicates no content was recorded.
        assertThat(liquidState.liquefiables.single().layer).isNull()
      }
      runOnIdle { size = 50.dp }
      runOnIdle {
        // Now it should be recorded.
        assertThat(liquidState.liquefiables.single().layer).isNotNull()
      }
    }
  }
}
