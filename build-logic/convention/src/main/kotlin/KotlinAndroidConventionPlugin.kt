// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import io.github.fletchmckee.buildlogic.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

@Suppress("unused") // Invoked reflectively
class KotlinAndroidConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.kotlin.android")

    extensions.configure<KotlinAndroidExtension> {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
      }
    }

    configureSpotless()
  }
}
