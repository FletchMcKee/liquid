// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.binary.compatibility.validator)
}

android {
  namespace = "io.github.fletchmckee.liquid"

  compileSdk = 36
  defaultConfig {
    minSdk = 23
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  testOptions {
    targetSdk = 36
    animationsDisabled = true
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

kotlin {
  explicitApi()
  addDefaultLiquidTargets()
  sourceSets {
    commonMain {
      dependencies {
        api(compose.ui)
        implementation(compose.foundation)
      }
    }

    val skikoMain by creating {
      dependsOn(commonMain.get())
    }

    iosMain {
      dependsOn(skikoMain)
    }

    macosMain {
      dependsOn(skikoMain)
    }

    jvmMain {
      dependsOn(skikoMain)
    }

    wasmJsMain {
      dependsOn(skikoMain)
    }

    jsMain {
      dependsOn(skikoMain)
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.assertk)

        @OptIn(ExperimentalComposeLibrary::class)
        implementation(compose.uiTest)
      }
    }

    jvmTest {
      dependencies {
        implementation(compose.desktop.currentOs)
      }
    }
  }
}

dependencies {
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
  configure(
    KotlinMultiplatform(
      // TODO: Set up dokka
      javadocJar = JavadocJar.None(),
      sourcesJar = true,
      androidVariantsToPublish = listOf("release"),
    ),
  )
}
