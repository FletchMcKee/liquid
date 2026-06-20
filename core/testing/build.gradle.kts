// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
  alias(libs.plugins.liquid.android.kotlin.multiplatform.library)
  alias(libs.plugins.roborazzi)
}

kotlin {
  android { namespace = "io.github.fletchmckee.liquid.core.testing" }

  iosArm64()
  iosSimulatorArm64()

  jvm()

  js {
    browser()
    binaries.executable()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    binaries.executable()
  }

  compilerOptions {
    freeCompilerArgs.addAll(
      "-opt-in=androidx.compose.ui.test.ExperimentalTestApi",
      "-opt-in=com.github.takahirom.roborazzi.ExperimentalRoborazziApi",
    )
  }

  sourceSets {
    commonMain.dependencies {
      api(libs.jetbrains.compose.uiTest)
    }

    androidMain.dependencies {
      implementation(libs.compose.test.manifest)
      implementation(libs.compose.junit4)
      implementation(libs.robolectric)
      implementation(libs.roborazzi.core)
      implementation(libs.roborazzi)
      implementation(libs.roborazzi.compose)
    }

    jvmMain.dependencies {
      implementation(compose.desktop.currentOs)
      implementation(libs.jetbrains.compose.uiTestJunit4)
      implementation(libs.roborazzi.core)
      implementation(libs.roborazzi.compose.desktop)
    }

    iosMain.dependencies {
      implementation(libs.roborazzi.compose.ios)
    }
  }
}
