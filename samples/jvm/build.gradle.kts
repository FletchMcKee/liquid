// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
}

kotlin {
  jvm()

  sourceSets {
    jvmMain.dependencies {
      implementation(projects.samples.composeApp)
      implementation(compose.desktop.currentOs)
      implementation(libs.kotlinx.coroutines.swing)
    }
  }
}

compose.desktop {
  application {
    mainClass = "io.github.fletchmckee.liquid.samples.jvm.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "io.github.fletchmckee.liquid.samples.jvm"
      packageVersion = "1.0.0"
    }
  }
}
