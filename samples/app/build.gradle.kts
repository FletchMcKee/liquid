// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
plugins {
  alias(libs.plugins.liquid.android.application)
  alias(libs.plugins.liquid.android.application.compose)
}

android {
  namespace = "io.github.fletchmckee.liquid.samples.draggable"

  defaultConfig {
    applicationId = "io.github.fletchmckee.liquid.samples.draggable"
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
    }
    create("benchmark") {
      initWith(buildTypes.getByName("release"))
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      isDebuggable = false
    }
  }
}

dependencies {
  implementation(projects.liquid)
  implementation(libs.activity.compose)
  implementation(libs.compose.material3)
  implementation(libs.compose.adaptive)
  implementation(libs.compose.adaptive.layout)
  implementation(libs.coil.compose)
  implementation(libs.coil.network.okhttp)
}
