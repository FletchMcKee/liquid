// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.fletchmckee.liquid.samples.app.displayNavIcons

@Composable
fun SliderScaffold(
  navController: NavController,
  modifier: Modifier = Modifier,
  frostProvider: (() -> Float)? = null,
  onFrostChange: (Float) -> Unit = {},
  useLiquidProvider: (() -> Boolean)? = null,
  onUseLiquidChange: (Boolean) -> Unit = {},
  content: @Composable (PaddingValues) -> Unit,
) = Scaffold(
  modifier = modifier,
  containerColor = Color.Transparent,
  contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom),
  topBar = {
    TopAppBar(
      navigationIcon = {
        if (displayNavIcons()) {
          IconButton(
            onClick = { navController.popBackStack() },
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back button",
              tint = MaterialTheme.colorScheme.onBackground,
            )
          }
        }
      },
      title = {
        frostProvider?.let {
          PrimarySlider(
            value = frostProvider(),
            onValueChange = onFrostChange,
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
          )
        }
      },
      actions = {
        useLiquidProvider?.let {
          Switch(
            checked = useLiquidProvider(),
            onCheckedChange = { onUseLiquidChange(it) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = MaterialTheme.colorScheme.primary,
              checkedTrackColor = SwitchDefaults.colors().uncheckedTrackColor,
              checkedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
              uncheckedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
          )
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
    )
  },
) { paddingValues ->
  content(paddingValues)
}
