// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
plugins {
  alias(libs.plugins.liquid.android.application)
  alias(libs.plugins.liquid.android.application.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.build.config)
}

android {
  namespace = "io.github.fletchmckee.liquid.samples.app"

  buildFeatures {
    buildConfig = true
  }

  defaultConfig {
    applicationId = "io.github.fletchmckee.liquid.samples.app"
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

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll(
      "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
    )
  }
}

buildConfig {
  buildConfigField("IS_CI", providers.environmentVariable("CI").isPresent)
}

dependencies {
  implementation(projects.liquid)
  implementation(libs.activity.compose)
  implementation(libs.compose.material3)
  implementation(libs.compose.material.icons.core)
  implementation(libs.compose.adaptive)
  implementation(libs.compose.adaptive.layout)
  implementation(libs.compose.navigation)
  implementation(libs.kotlinx.serialization)
  implementation(libs.coil.compose)
  implementation(libs.coil.network.okhttp)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.rule)
  testImplementation(libs.roborazzi.core)
  testImplementation(libs.coil.test)
}

roborazzi {
  outputDir.set(project.layout.projectDirectory.dir("screenshots"))
}
