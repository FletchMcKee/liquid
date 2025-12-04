// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

@Suppress("unused") // Invoked reflectively
class AndroidKmpLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    apply(plugin = "com.android.kotlin.multiplatform.library")

    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
      extensions.configure<KotlinMultiplatformExtension> {
        @Suppress("UnstableApiUsage")
        androidLibrary {
          compileSdk = 36
          minSdk = 23

          withDeviceTestBuilder {
            sourceSetTreeName = KotlinSourceSetTree.test.name
          }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
          }

          compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
          }
        }
      }
    }
  }
}
