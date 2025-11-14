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

/**
 * Essentially nothing can be shared in commonMain when it comes to video,
 * so instead we'll ask the platforms to provide a full composable or null.
 */
expect fun platformVideoPlayer(): (@Composable () -> Unit)?
