// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
}

kotlin {
  @OptIn(ExperimentalWasmDsl::class)
  js {
    browser()
    binaries.executable()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    binaries.executable()
  }

  sourceSets {
    webMain.dependencies {
      implementation(projects.samples.composeApp)
      implementation(projects.liquid)
      implementation(libs.jetbrains.compose.ui)
      implementation(libs.jetbrains.material3)
      implementation(libs.jetbrains.navigation.compose)
      implementation(libs.jetbrains.components.resources)
    }
  }
}
