// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.grid

import android.os.Build
import android.view.RoundedCorner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.samples.app.utils.safeShadow
import io.github.fletchmckee.liquid.samples.app.utils.thenIf

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
  frostProvider: () -> Float,
  modifier: Modifier = Modifier,
  useLiquid: Boolean = true,
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
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
          this.frost = frostProvider().dp
          this.shape = shape
          this.refraction = 0.4f
          this.curve = 0.15f
          this.edge = 0.1f
          this.tint = containerColor
        }
      },
    title = title,
    navigationIcon = navigationIcon,
    actions = actions,
    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
  )
}

@Composable
fun LiquidBottomAppBar(
  liquidState: LiquidState,
  frostProvider: () -> Float,
  modifier: Modifier = Modifier,
  useLiquid: Boolean = true,
  shape: Shape = RoundedCornerShape(35),
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  content: @Composable () -> Unit = {},
) = Row(
  modifier = modifier
    .fillMaxWidth()
    .padding(24.dp)
    .safeShadow(elevation = 4.dp, shape = shape)
    .thenIf(useLiquid) {
      liquid(liquidState) {
        this.frost = frostProvider().dp
        this.shape = shape
        this.refraction = 0.4f
        this.curve = 0.15f
        this.edge = 0.1f
        this.tint = containerColor
      }
    },
) {
  content()
}
