// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
plugins {
  alias(libs.plugins.liquid.android.library)
  alias(libs.plugins.liquid.android.library.compose)
  alias(libs.plugins.binary.compatibility.validator)
}

android {
  namespace = "io.github.fletchmckee.liquid"
}

kotlin {
  explicitApi()
}

dependencies {
  api(libs.compose.ui)
  implementation(libs.compose.foundation)
}
