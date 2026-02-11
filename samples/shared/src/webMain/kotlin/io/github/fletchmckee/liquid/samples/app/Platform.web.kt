// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import coil3.PlatformContext
import okio.Path

actual fun displayNavIcons(): Boolean = false

actual fun PlatformContext.cacheDir(): Path? = null
