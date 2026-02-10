// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
  alias(libs.plugins.liquid.android.kotlin.multiplatform.library)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.roborazzi)
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll(
      "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
    )
  }

  androidLibrary {
    namespace = "io.github.fletchmckee.liquid.samples.shared"
    androidResources.enable = true

    withHostTest {
      isIncludeAndroidResources = true
    }
  }

  listOf(
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { iosTarget ->
    iosTarget.binaries.framework(buildTypes = setOf(NativeBuildType.DEBUG)) {
      baseName = "ComposeApp"
      isStatic = true
    }

    // Enables calling Swift code from Kotlin for the WebVIew.
    iosTarget.compilations.named("main") {
      cinterops.register("SwiftGlassWebViewProvider") {
        val glassDef = project.layout.projectDirectory.file("../ios/App/Interops/SwiftGlassWebViewProvider.def")
        definitionFile.set(glassDef)
        val interOpsDir = project.layout.projectDirectory.dir("../ios/App/Interops/")
        includeDirs {
          allHeaders(interOpsDir)
        }
      }
    }
  }

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

  sourceSets {
    commonMain.dependencies {
      api(libs.jetbrains.compose.foundation)
      implementation(projects.liquid)
      implementation(libs.jetbrains.compose.runtime)
      implementation(libs.jetbrains.material3)
      implementation(libs.jetbrains.compose.ui)
      implementation(libs.jetbrains.components.resources)
      implementation(libs.compose.material.icons)
      implementation(libs.jetbrains.navigation.compose)
      implementation(libs.kotlinx.serialization)
      implementation(libs.coil.compose)
      implementation(libs.coil.ktor)
      implementation(libs.ktor.core)
      implementation(libs.jetbrains.lifecycle.runtimeCompose)
      implementation(libs.jetbrains.material3.adaptive)
    }

    androidMain.dependencies {
      implementation(libs.ktor.cio)
      implementation(libs.androidx.media3.exoplayer)
      implementation(libs.androidx.media3.ui.compose)
    }

    iosMain.dependencies {
      implementation(libs.ktor.darwin)
    }

    jvmMain.dependencies {
      implementation(libs.ktor.cio)
      implementation(libs.kotlinx.coroutines.swing)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(projects.core.testing)
    }
  }
}

compose.resources { publicResClass = true }

roborazzi {
  outputDir.set(project.layout.projectDirectory.dir("screenshots"))
}
