// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SliderScaffold(
  navController: NavController,
  modifier: Modifier = Modifier,
  frostProvider: (() -> Float)? = null,
  onFrostChange: (Float) -> Unit = {},
  content: @Composable (PaddingValues) -> Unit,
) = Scaffold(
  modifier = modifier,
  containerColor = Color.Transparent,
  contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom),
  topBar = {
    TopAppBar(
      navigationIcon = {
        IconButton(
          onClick = { navController.navigateUp() },
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back button",
            tint = MaterialTheme.colorScheme.onBackground,
          )
        }
      },
      title = {
        frostProvider?.let {
          Slider(
            value = frostProvider(),
            onValueChange = onFrostChange,
            valueRange = 0f..50f,
            thumb = {
              Box(
                Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary),
              )
            },
            track = { state ->
              SliderDefaults.Track(
                sliderState = state,
                drawStopIndicator = null,
                drawTick = { _, _ -> },
                modifier = Modifier.height(8.dp),
              )
            },
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
          )
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
    )
  },
) { paddingValues ->
  content(paddingValues)
}
