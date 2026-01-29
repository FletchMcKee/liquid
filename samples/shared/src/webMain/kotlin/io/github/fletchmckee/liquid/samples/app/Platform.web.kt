// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import coil3.PlatformContext
import okio.Path

// `bindToBrowserNavigation` still has issues.
actual fun displayNavIcons(): Boolean = true

actual fun PlatformContext.cacheDir(): Path? = null
