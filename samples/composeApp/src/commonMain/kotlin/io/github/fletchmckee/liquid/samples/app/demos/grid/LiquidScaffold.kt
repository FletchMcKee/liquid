// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.grid

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.samples.app.theme.LiquidShadow
import io.github.fletchmckee.liquid.samples.app.utils.thenIf

@Composable
internal expect fun rememberTopBarShape(): Shape

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
  topBarShape: Shape = rememberTopBarShape(),
  title: @Composable () -> Unit = {},
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
) {
  TopAppBar(
    modifier = modifier
      .fillMaxWidth()
      .thenIf(useLiquid) {
        liquid(liquidState) {
          frost = frostProvider().dp
          refraction = 0.25f
          curve = 0.5f
          shape = topBarShape
          edge = 0.05f
          tint = containerColor
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
  bottomBarShape: Shape = CircleShape,
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  content: @Composable () -> Unit = {},
) = Row(
  modifier = modifier
    .widthIn(600.dp)
    .padding(24.dp)
    .dropShadow(bottomBarShape, LiquidShadow)
    .thenIf(useLiquid) {
      liquid(liquidState) {
        frost = frostProvider().dp
        refraction = 0.25f
        curve = 0.5f
        shape = bottomBarShape
        edge = 0.1f
        tint = containerColor
        saturation = 1.5f
        dispersion = 0.2f
      }
    },
) {
  content()
}
