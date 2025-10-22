// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
plugins {
  alias(libs.plugins.liquid.root)
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.android.test) apply false
  alias(libs.plugins.binary.compatibility.validator) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.compose.hotReload) apply false
  alias(libs.plugins.dokka) apply false
}
