// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.buildlogic

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasUnitTestBuilder
import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

fun Project.configureAndroid() {
  android {
    compileSdkVersion(36)

    defaultConfig {
      minSdk = 23
      targetSdk = 36

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

      testOptions {
        unitTests {
          isIncludeAndroidResources = true
        }
      }
    }

    packagingOptions {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
    }
  }

  pluginManager.withPlugin("org.jetbrains.kotlin.android") {
    configure<KotlinAndroidProjectExtension> {
      val warningsAsErrors =
        providers
          .gradleProperty("warningsAsErrors")
          .map {
            it.toBoolean()
          }.orElse(false)

      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        allWarningsAsErrors = warningsAsErrors
        freeCompilerArgs.addAll(
          listOf(
            // Enable experimental coroutines APIs, including Flow
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
          ),
        )
      }
    }
  }

  androidComponents {
    beforeVariants(selector().withBuildType("release")) { variantBuilder ->
      (variantBuilder as? HasUnitTestBuilder)?.apply {
        enableUnitTest = false
      }
    }

    beforeVariants(selector().withBuildType("benchmark")) { variantBuilder ->
      (variantBuilder as? HasUnitTestBuilder)?.apply {
        enableUnitTest = false
      }
    }
  }
}

private fun Project.android(action: BaseExtension.() -> Unit) = extensions.configure<BaseExtension>(action)

private fun Project.androidComponents(action: AndroidComponentsExtension<*, *, *>.() -> Unit) {
  extensions.configure(AndroidComponentsExtension::class.java, action)
}
