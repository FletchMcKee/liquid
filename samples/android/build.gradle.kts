// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
plugins {
  alias(libs.plugins.liquid.android.application)
  alias(libs.plugins.liquid.compose.multiplatform)
}

android {
  namespace = "io.github.fletchmckee.liquid.samples.android"

  defaultConfig {
    applicationId = "io.github.fletchmckee.liquid.samples.android"
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
    }

    register("benchmark") {
      initWith(buildTypes.getByName("release"))
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      isDebuggable = false
    }
  }

  testOptions {
    animationsDisabled = true
  }
}

dependencies {
  implementation(projects.samples.shared)
  implementation(libs.activity.compose)
}
