// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import android.os.Build
import coil3.PlatformContext
import okio.Path
import okio.Path.Companion.toOkioPath

class AndroidPlatform : Platform {
  override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun PlatformContext.cacheDir(): Path? = this.cacheDir.toOkioPath()

internal actual fun displayNavIcons(): Boolean = false
