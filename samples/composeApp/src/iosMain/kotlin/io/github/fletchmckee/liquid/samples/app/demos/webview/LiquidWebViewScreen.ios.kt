// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalForeignApi::class)

package io.github.fletchmckee.liquid.samples.app.demos.webview

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.navigation.NavController
import io.github.fletchmckee.liquid.samples.app.SwiftGlassWebViewProvider
import io.github.fletchmckee.liquid.samples.app.common.SliderScaffold
import kotlinx.cinterop.ExperimentalForeignApi

@Composable
fun LiquidWebViewScreen(
  navController: NavController,
  modifier: Modifier = Modifier,
  url: String = "https://www.google.com",
) = SliderScaffold(
  navController = navController,
  modifier = modifier,
) { paddingValues ->
  UIKitViewController(
    factory = {
      SwiftGlassWebViewProvider.createWebViewGlass(url)
    },
    modifier = modifier.padding(paddingValues),
  )
}
