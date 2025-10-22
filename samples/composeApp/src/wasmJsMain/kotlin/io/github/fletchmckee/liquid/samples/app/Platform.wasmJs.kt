// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import coil3.PlatformContext
import okio.Path

class WasmPlatform : Platform {
  override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun PlatformContext.cacheDir(): Path? = null
