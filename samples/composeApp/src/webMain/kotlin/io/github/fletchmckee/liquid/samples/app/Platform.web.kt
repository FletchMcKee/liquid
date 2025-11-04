// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.PlatformContext
import okio.Path

external interface MediaQueryList {
  val matches: Boolean
}

external interface Window {
  fun matchMedia(query: String): MediaQueryList
}

external val window: Window

actual fun displayNavIcons(): Boolean = false

actual fun PlatformContext.cacheDir(): Path? = null

@Composable
actual fun rememberPullToRefreshEnabled(): Boolean = remember {
  window.matchMedia(query = "(pointer: coarse)").matches ||
    window.matchMedia(query = "(hover: none)").matches
}
