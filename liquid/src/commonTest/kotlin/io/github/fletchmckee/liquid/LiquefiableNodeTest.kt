// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTestApi::class)

package io.github.fletchmckee.liquid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isSameInstanceAs
import io.github.fletchmckee.liquid.internal.LiquefiableNode
import kotlin.test.BeforeTest
import kotlin.test.Test

class LiquefiableNodeTest {
  private lateinit var liquidState: LiquidState

  @BeforeTest fun setUp() {
    liquidState = LiquidState()
  }

  @Test fun removingAndAddingSameLiquefiable_doesNotCreateLiquidStateDuplicates() = runComposeUiTest {
    val liquefiableNode = LiquefiableNode(liquidState)
    var showLiquefiable by mutableStateOf(true)
    var liquefiableDrawCount = 0
    var liquidDrawCount = 0
    setContent {
      Parent {
        if (showLiquefiable) {
          Box(
            Modifier
              .size(50.dp)
              .elementOf(liquefiableNode)
              .drawBehind { liquefiableDrawCount++ },
          )
        }

        Box(
          Modifier
            .size(100.dp)
            .liquid(liquidState)
            .drawBehind { liquidDrawCount++ },
        )
      }
    }

    lateinit var graphicsLayer: GraphicsLayer
    runOnIdle {
      // onAttach and onGloballyPositioned
      assertThat(liquefiableDrawCount).isEqualTo(2)
      // onAttach and the Liquefiable being added
      assertThat(liquidDrawCount).isEqualTo(2)
      assertThat(liquidState.liquefiables.size).isEqualTo(1)
      graphicsLayer = liquefiableNode.liquefiable.layer!!
    }
    runOnIdle { showLiquefiable = false }
    runOnIdle {
      // Should see no increment to the liquefiable draw count, but the liquidDrawCount should be updated.
      assertThat(liquefiableDrawCount).isEqualTo(2)
      assertThat(liquidDrawCount).isEqualTo(3)
      assertThat(liquidState.liquefiables).isEmpty()
      assertThat(liquefiableNode.liquefiable.layer).isNull()
      assertThat(liquefiableNode.liquefiable.boundsOnScreen).isEqualTo(Rect.Zero)
    }
    runOnIdle { showLiquefiable = true }
    runOnIdle {
      assertThat(liquefiableDrawCount).isEqualTo(4)
      // onAttach and the Liquefiable being added again
      assertThat(liquidDrawCount).isEqualTo(5)
      assertThat(liquidState.liquefiables.size).isEqualTo(1)
      assertThat(liquefiableNode.liquefiable.layer).isNotEqualTo(graphicsLayer)
    }
  }

  @Test fun graphicsLayerRemainsUnchanged_boundsOnScreenUpdates() = runComposeUiTest {
    val liquefiableNode = LiquefiableNode(liquidState)
    var offset by mutableStateOf(IntOffset.Zero)
    var coords: LayoutCoordinates? = null
    var drawCount = 0
    setContent {
      Parent {
        Box(
          Modifier
            .size(50.dp)
            .offset { offset }
            .elementOf(liquefiableNode)
            .onGloballyPositioned { coords = it }
            .drawBehind { drawCount++ },
        )
      }
    }

    lateinit var graphicsLayer: GraphicsLayer
    runOnIdle {
      assertThat(drawCount).isEqualTo(1)
      assertThat(liquidState.liquefiables.size).isEqualTo(1)
      val expectedBounds = Rect(coords!!.positionOnScreen(), coords.size.toSize())
      assertThat(liquefiableNode.liquefiable.boundsOnScreen)
        .isEqualTo(expectedBounds)
      graphicsLayer = liquefiableNode.liquefiable.layer!!
    }
    runOnIdle { offset = IntOffset(10, 10) }
    runOnIdle {
      // Position changes will update a liquidNode if present to redraw, but not the liquefiable node itself.
      assertThat(drawCount).isEqualTo(1)
      assertThat(liquidState.liquefiables.size).isEqualTo(1)
      // Verify the graphicsLayer remains unchanged despite the offset update.
      assertThat(liquefiableNode.liquefiable.layer).isSameInstanceAs(graphicsLayer)
    }
  }

  @Test fun positionChanges_invalidateLiquidNodeDraw_butNotLiquefiableNodeDraw() = runComposeUiTest {
    val liquefiableNode = LiquefiableNode(liquidState)
    var offset by mutableStateOf(IntOffset.Zero)
    var liquefiableDrawCount = 0
    var liquidDrawCount = 0
    setContent {
      Parent {
        Box(
          Modifier
            .size(50.dp)
            .offset { offset }
            .elementOf(liquefiableNode)
            .drawBehind { liquefiableDrawCount++ },
        )

        Box(
          Modifier
            .size(100.dp)
            .liquid(liquidState)
            .drawBehind { liquidDrawCount++ },
        )
      }
    }

    runOnIdle {
      // Needs further looking into, but when you have an offset modifier like the one above before the
      // liquefiable node, the onAttach and onGloballyPositioned appear to batch the draw into a single pass.
      assertThat(liquefiableDrawCount).isEqualTo(1)
      // onAttach and Liquefiable being added
      assertThat(liquidDrawCount).isEqualTo(2)
    }
    runOnIdle { offset = IntOffset(10, 10) }
    runOnIdle {
      // liquid node increments, but liquefiable remains unchanged.
      assertThat(liquefiableDrawCount).isEqualTo(1)
      assertThat(liquidDrawCount).isEqualTo(3)
    }
  }

  @Test fun emptySizeLiquefiable_doesNotRecordContent() = runComposeUiTest {
    var size by mutableStateOf(0.dp)
    val liquefiableNode = LiquefiableNode(liquidState)
    var drawCount = 0
    setContent {
      Parent {
        Box(
          Modifier
            .size(size)
            .elementOf(liquefiableNode)
            .drawBehind { drawCount++ },
        )
      }
    }

    runOnIdle {
      assertThat(drawCount).isEqualTo(1)
      // A null graphicsLayer indicates no content was recorded.
      assertThat(liquidState.liquefiables.single().layer).isNull()
    }
    runOnIdle { size = 50.dp }
    runOnIdle {
      assertThat(drawCount).isEqualTo(2)
      // Now it should be recorded.
      assertThat(liquidState.liquefiables.single().layer).isNotNull()
    }
  }
}
