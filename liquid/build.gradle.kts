// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import kotlinx.validation.ExperimentalBCVApi
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
  alias(libs.plugins.liquid.android.kotlin.multiplatform.library)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.binary.compatibility.validator)
  alias(libs.plugins.dokka)
}

kotlin {
  explicitApi()
  addDefaultLiquidTargets()

  compilerOptions { allWarningsAsErrors = true }
  // Convention plugin sets everything else.
  androidLibrary { namespace = "io.github.fletchmckee.liquid" }

  sourceSets {
    commonMain.dependencies {
      api(libs.jetbrains.compose.ui)
      implementation(libs.jetbrains.compose.foundation)
    }

    val skikoMain by creating { dependsOn(commonMain.get()) }

    iosMain { dependsOn(skikoMain) }
    macosMain { dependsOn(skikoMain) }
    jvmMain { dependsOn(skikoMain) }
    wasmJsMain { dependsOn(skikoMain) }
    jsMain { dependsOn(skikoMain) }

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

tasks.withType<KotlinJsTest>().configureEach {
  enabled = false
}

apiValidation {
  @OptIn(ExperimentalBCVApi::class)
  klib { enabled = true }
}

mavenPublishing {
  configure(
    KotlinMultiplatform(
      javadocJar = JavadocJar.Dokka("dokkaGenerate"),
      sourcesJar = true,
    ),
  )
}
