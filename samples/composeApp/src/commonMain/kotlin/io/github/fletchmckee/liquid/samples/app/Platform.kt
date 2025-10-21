// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import coil3.PlatformContext
import okio.Path

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform

internal expect fun PlatformContext.cacheDir(): Path?

internal expect fun displayNavIcons(): Boolean
