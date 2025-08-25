// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.fletchmckee.liquid.samples.draggable.ui.grid

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
) = TopAppBar(
  modifier = modifier
    .fillMaxWidth()
    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
    .thenIf(useLiquid) {
      liquid(
        liquidState = liquidState,
        frost = initialFrost.dp,
      )
    }
    .padding(bottom = 24.dp),
  title = title,
  navigationIcon = navigationIcon,
  actions = actions,
  colors = TopAppBarDefaults.topAppBarColors(
    containerColor = Color.Transparent,
  ),
)
