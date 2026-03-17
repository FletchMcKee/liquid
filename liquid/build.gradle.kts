// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
  alias(libs.plugins.liquid.android.kotlin.multiplatform.library)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.dokka)
}

kotlin {
  explicitApi()

  @OptIn(ExperimentalAbiValidation::class)
  abiValidation { enabled = true }

  addDefaultLiquidTargets()

  compilerOptions { allWarningsAsErrors = true }
  // Convention plugin sets everything else.
  android {
    namespace = "io.github.fletchmckee.liquid"
    androidResources.enable = true
  }

  sourceSets {
    commonMain.dependencies {
      api(libs.jetbrains.compose.ui)
      implementation(libs.jetbrains.compose.foundation)
    }

    val skikoMain by registering { dependsOn(commonMain.get()) }

    iosMain { dependsOn(skikoMain.get()) }
    macosMain { dependsOn(skikoMain.get()) }
    jvmMain { dependsOn(skikoMain.get()) }
    wasmJsMain { dependsOn(skikoMain.get()) }
    jsMain { dependsOn(skikoMain.get()) }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.assertk)
      implementation(libs.jetbrains.compose.uiTest)
    }

    androidDeviceTest.dependencies {
      implementation(libs.androidx.junit)
      implementation(libs.compose.test.manifest)
    }

    jvmTest.dependencies {
      implementation(compose.desktop.currentOs)
    }

    // For some reason you can't add this to commonTest or in the classes themselves.
    wasmJsTest.languageSettings {
      optIn("kotlin.js.ExperimentalWasmJsInterop")
    }
  }
}

dependencies {
  // This is for backwards compatibility. Can be removed in 2.0.0.
  constraints {
    "androidMainApi"("androidx.compose.ui:ui:1.9.3") {
      because("Library now uses JetBrains Compose Multiplatform")
    }
    "androidMainImplementation"("androidx.compose.foundation:foundation:1.9.3") {
      because("Library now uses JetBrains Compose Multiplatform")
    }
  }
}

mavenPublishing {
  configure(KotlinMultiplatform(javadocJar = JavadocJar.Dokka("dokkaGenerate")))
}
