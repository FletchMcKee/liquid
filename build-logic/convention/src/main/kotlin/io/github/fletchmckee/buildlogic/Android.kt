// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.buildlogic

import LiquidVersions
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.TestExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the

// TODO: Figure out a better way of setting this up now that BaseExtension is no longer available.
fun Project.configureAndroidApp() {
  val liquidVersions = the<LiquidVersions>()
  extensions.configure<ApplicationExtension> {
    compileSdk = liquidVersions.compileSdk.get()

    defaultConfig {
      minSdk = liquidVersions.minSdk.get()
      targetSdk = liquidVersions.compileSdk.get()

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
      sourceCompatibility = JavaVersion.toVersion(liquidVersions.jdk.get())
      targetCompatibility = JavaVersion.toVersion(liquidVersions.jdk.get())
    }
  }
}

fun Project.configureAndroidTest() {
  val liquidVersions = the<LiquidVersions>()
  extensions.configure<TestExtension> {
    compileSdk = liquidVersions.compileSdk.get()

    defaultConfig {
      minSdk = liquidVersions.minSdk.get()
      targetSdk = liquidVersions.compileSdk.get()
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }

    compileOptions {
      sourceCompatibility = JavaVersion.toVersion(liquidVersions.jdk.get())
      targetCompatibility = JavaVersion.toVersion(liquidVersions.jdk.get())
    }
  }
}
