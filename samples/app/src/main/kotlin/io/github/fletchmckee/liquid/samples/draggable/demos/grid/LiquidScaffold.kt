// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.fletchmckee.liquid.samples.draggable.demos.grid

import android.os.Build
import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.samples.draggable.utils.thenIf

@Composable
fun LiquidScaffold(
  modifier: Modifier = Modifier,
  topAppBar: @Composable () -> Unit = {},
  bottomAppBar: @Composable () -> Unit = {},
  content: @Composable (PaddingValues) -> Unit,
) = Scaffold(
  containerColor = Color.Transparent,
  topBar = topAppBar,
  bottomBar = bottomAppBar,
  modifier = modifier,
) { padding ->
  content(padding)
}

@Composable
fun LiquidTopAppBar(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  useLiquid: Boolean = true,
  initialFrost: Float = 10f,
  title: @Composable () -> Unit = {},
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
) {
  val view = LocalView.current
  val insets = view.rootWindowInsets
  val shape = remember(insets) {
    when {
      Build.VERSION.SDK_INT >= 31 -> {
        RoundedCornerShape(
          topStart = insets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius?.toFloat() ?: 0f,
          topEnd = insets.getRoundedCorner(RoundedCorner.POSITION_TOP_RIGHT)?.radius?.toFloat() ?: 0f,
          bottomStart = 0f,
          bottomEnd = 0f,
        )
      }
      else -> RectangleShape
    }
  }

  TopAppBar(
    modifier = modifier
      .fillMaxWidth()
      .thenIf(useLiquid) {
        liquid(liquidState) {
          this.frost = initialFrost.dp
          this.shape = shape
          this.refraction = 0.25f
          this.curve = 0.1f
          this.edge = 0.1f
        }
      }
      .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = shape),
    title = title,
    navigationIcon = navigationIcon,
    actions = actions,
    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
  )
}

@Composable
fun LiquidBottomAppBar(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  useLiquid: Boolean = true,
  initialFrost: Float = 10f,
  shape: Shape = RoundedCornerShape(35),
  content: @Composable () -> Unit = {},
) = Row(
  modifier = modifier
    .fillMaxWidth()
    .padding(24.dp)
    .shadow(elevation = 4.dp, shape = shape)
    .thenIf(useLiquid) {
      liquid(liquidState) {
        this.frost = initialFrost.dp
        this.shape = shape
        this.refraction = 0.25f
        this.curve = 0.1f
        this.edge = 0.1f
      }
    }
    .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = shape),
) {
  content()
}
