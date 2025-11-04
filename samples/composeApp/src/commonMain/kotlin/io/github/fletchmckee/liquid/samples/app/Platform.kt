// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.runtime.Composable
import coil3.PlatformContext
import okio.Path

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform

expect fun PlatformContext.cacheDir(): Path?

expect fun displayNavIcons(): Boolean

@Composable
expect fun rememberPullToRefreshEnabled(): Boolean
