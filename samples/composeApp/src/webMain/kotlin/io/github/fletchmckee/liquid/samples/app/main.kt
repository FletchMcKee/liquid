// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(
  ExperimentalComposeUiApi::class,
  ExperimentalBrowserHistoryApi::class,
)

package io.github.fletchmckee.liquid.samples.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.compose.rememberNavController

fun main() {
  ComposeViewport("ComposeApp") {
    val navController = rememberNavController()
    LiquidDemos(navController = navController)
    LaunchedEffect(Unit) {
      navController.bindToBrowserNavigation()
    }
  }
}
