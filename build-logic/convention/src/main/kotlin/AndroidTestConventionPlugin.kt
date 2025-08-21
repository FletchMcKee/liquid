// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.gradle.TestExtension
import io.github.fletchmckee.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

@Suppress("unused") // Invoked reflectively
class AndroidTestConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    apply(plugin = "com.android.test")
    apply(plugin = "org.jetbrains.kotlin.android")

    extensions.configure<TestExtension> {
      configureKotlinAndroid(this)
      defaultConfig.targetSdk = 36
      defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
  }
}
