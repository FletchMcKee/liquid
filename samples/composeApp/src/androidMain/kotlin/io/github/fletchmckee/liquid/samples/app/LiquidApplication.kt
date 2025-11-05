// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app

import android.app.Application
import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi

class LiquidApplication : Application() {
  @OptIn(ExperimentalFoundationApi::class)
  override fun onCreate() {
    ComposeFoundationFlags.isPausableCompositionInPrefetchEnabled = true
    super.onCreate()
  }
}
