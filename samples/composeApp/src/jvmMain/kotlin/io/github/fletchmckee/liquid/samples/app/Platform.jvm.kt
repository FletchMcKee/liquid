// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.runtime.Composable
import coil3.PlatformContext
import okio.Path

class JVMPlatform : Platform {
  override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun PlatformContext.cacheDir(): Path? = null

actual fun displayNavIcons(): Boolean = true

@Composable
actual fun rememberPullToRefreshEnabled(): Boolean = false
