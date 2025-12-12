// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.variant.HasUnitTestBuilder
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.liquid.android.application)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.compose.hotReload)
  alias(libs.plugins.roborazzi)
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll(
      "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
      "-opt-in=kotlin.time.ExperimentalTime",
    )
  }

  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
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
        definitionFile.set(
          project.layout.projectDirectory.file("../ios/App/Interops/SwiftGlassWebViewProvider.def"),
        )
        includeDirs(
          project.layout.projectDirectory
            .dir("../ios/App/Interops/")
            .asFile,
        )
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
      implementation(projects.liquid)
      implementation(libs.jetbrains.compose.runtime)
      implementation(libs.jetbrains.compose.foundation)
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
      implementation(libs.activity.compose)
      implementation(libs.androidx.media3.exoplayer)
      implementation(libs.androidx.media3.ui.compose)
    }

    iosMain.dependencies {
      implementation(libs.ktor.darwin)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(projects.core.testing)
    }

    jvmMain.dependencies {
      implementation(libs.ktor.cio)
      implementation(compose.desktop.currentOs)
      implementation(libs.kotlinx.coroutines.swing)
    }
  }
}

android {
  namespace = "io.github.fletchmckee.liquid.samples.app"

  defaultConfig {
    applicationId = "io.github.fletchmckee.liquid.samples.app"
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }

    create("benchmark") {
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

androidComponents {
  beforeVariants(selector().withBuildType("release")) { variantBuilder ->
    variantBuilder.enable = false
  }

  beforeVariants(selector().withBuildType("benchmark")) { variantBuilder ->
    (variantBuilder as? HasUnitTestBuilder)?.apply {
      enableUnitTest = false
    }
  }
}

compose.desktop {
  application {
    mainClass = "io.github.fletchmckee.liquid.samples.app.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "io.github.fletchmckee.liquid.samples.app"
      packageVersion = "1.0.0"
    }
  }
}

tasks.withType<KotlinJsTest> {
  enabled = false
}

tasks.withType<Test> {
  failOnNoDiscoveredTests.set(false)
}

roborazzi {
  outputDir.set(project.layout.projectDirectory.dir("screenshots"))
}
