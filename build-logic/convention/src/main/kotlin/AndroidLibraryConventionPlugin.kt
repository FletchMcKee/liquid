// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.gradle.LibraryExtension
import io.github.fletchmckee.buildlogic.configureKotlinAndroid
import io.github.fletchmckee.buildlogic.configureSpotless
import io.github.fletchmckee.buildlogic.configureTesting
import io.github.fletchmckee.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused") // Invoked reflectively
class AndroidLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    apply(plugin = "com.android.library")
    apply(plugin = "org.jetbrains.kotlin.android")

    configureSpotless()
    configureTesting()

    extensions.configure<LibraryExtension> {
      configureKotlinAndroid(this)
      defaultConfig.targetSdk = 36
      defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      testOptions.animationsDisabled = true

      resourcePrefix =
        path
          .split("""\W""".toRegex())
          .drop(1)
          .distinct()
          .joinToString(separator = "_")
          .lowercase()
          .plus("_")
    }

    dependencies {
      "androidTestImplementation"(libs.findLibrary("kotlin-test").get())
      "testImplementation"(libs.findLibrary("kotlin-test").get())
    }
  }
}
