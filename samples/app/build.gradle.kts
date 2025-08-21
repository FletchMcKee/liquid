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

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
  implementation(libs.compose.adaptive)
  implementation(libs.compose.adaptive.layout)
}
