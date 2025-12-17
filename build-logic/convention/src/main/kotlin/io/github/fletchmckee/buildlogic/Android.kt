// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.HasUnitTestBuilder
import com.android.build.api.variant.TestAndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// TODO: Figure out a better way of setting this up now that BaseExtension is no longer available.
fun Project.configureAndroidApp() {
  extensions.configure<ApplicationExtension> {
    compileSdk = Versions.CompileSdk

    defaultConfig {
      minSdk = Versions.MinSdk
      targetSdk = Versions.CompileSdk

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

      testOptions {
        unitTests {
          isIncludeAndroidResources = true
        }
      }
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }

    compileOptions {
      sourceCompatibility = Versions.Java
      targetCompatibility = Versions.Java
    }
  }

  extensions.configure<ApplicationAndroidComponentsExtension> {
    beforeVariants(selector().withBuildType("release")) { variantBuilder ->
      (variantBuilder as? HasUnitTestBuilder)?.enableUnitTest = false
    }

    beforeVariants(selector().withBuildType("benchmark")) { variantBuilder ->
      (variantBuilder as? HasUnitTestBuilder)?.enableUnitTest = false
    }
  }
}

fun Project.configureAndroidTest() {
  extensions.configure<TestExtension> {
    compileSdk = Versions.CompileSdk

    defaultConfig {
      minSdk = Versions.MinSdk
      targetSdk = Versions.CompileSdk
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }

    compileOptions {
      sourceCompatibility = Versions.Java
      targetCompatibility = Versions.Java
    }
  }

  extensions.configure<TestAndroidComponentsExtension> {
    beforeVariants(selector().withBuildType("release")) { variantBuilder ->
      (variantBuilder as? HasUnitTestBuilder)?.enableUnitTest = false
    }

    beforeVariants(selector().withBuildType("benchmark")) { variantBuilder ->
      (variantBuilder as? HasUnitTestBuilder)?.enableUnitTest = false
    }
  }
}
