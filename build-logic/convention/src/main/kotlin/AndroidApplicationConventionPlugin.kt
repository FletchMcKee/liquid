// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.dsl.ApplicationExtension
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
class AndroidApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    apply(plugin = "com.android.application")
    apply(plugin = "org.jetbrains.kotlin.android")

    configureSpotless()
    configureTesting()

    extensions.configure<ApplicationExtension> {
      configureKotlinAndroid(this)
      defaultConfig.targetSdk = 36
      defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      testOptions.animationsDisabled = true
      // For Robolectric
      testOptions.unitTests.isIncludeAndroidResources = true
    }

    dependencies {
      "testImplementation"(libs.findLibrary("junit").get())
      "testImplementation"(libs.findLibrary("robolectric").get())
    }
  }
}
