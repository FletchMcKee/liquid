// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.liquid.android.library)
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
  alias(libs.plugins.roborazzi)
}

android {
  namespace = "io.github.fletchmckee.liquid.core.testing"
}

kotlin {
  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }

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
      api(compose.uiTest)
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
      implementation(compose.desktop.uiTestJUnit4)
      implementation(libs.roborazzi.core)
      implementation(libs.roborazzi.compose.desktop)
    }

    iosMain.dependencies {
      implementation(libs.roborazzi.compose.ios)
    }
  }
}

tasks.withType<Test> {
  failOnNoDiscoveredTests.set(false)
}
