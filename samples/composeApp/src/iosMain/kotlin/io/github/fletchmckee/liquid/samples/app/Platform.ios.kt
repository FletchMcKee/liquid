// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import androidx.compose.runtime.Composable
import coil3.PlatformContext
import okio.Path
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
  override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun PlatformContext.cacheDir(): Path? = null

actual fun displayNavIcons(): Boolean = true

@Composable
actual fun rememberPullToRefreshEnabled(): Boolean = true
