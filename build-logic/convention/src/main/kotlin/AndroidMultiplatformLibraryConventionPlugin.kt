// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import io.github.fletchmckee.buildlogic.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

@Suppress("unused") // Invoked reflectively
class AndroidMultiplatformLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    apply(plugin = "com.android.kotlin.multiplatform.library")

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
      extensions.configure<KotlinMultiplatformExtension> {
        targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
          compileSdk = Versions.CompileSdk
          minSdk = Versions.MinSdk

          withDeviceTestBuilder {
            sourceSetTreeName = KotlinSourceSetTree.test.name
          }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
          }

          packaging {
            resources {
              excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
          }

          compilerOptions {
            jvmTarget.set(Versions.Jvm)
          }
        }
      }
    }
  }
}
