// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.dsl.KotlinMultiplatformAndroidDeviceTestCompilation
import com.android.build.api.dsl.KotlinMultiplatformAndroidHostTestCompilation
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused") // Invoked reflectively
class AndroidMultiplatformLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    apply(plugin = "com.android.kotlin.multiplatform.library")

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
      val liquidVersions = the<LiquidVersions>()
      extensions.configure<KotlinMultiplatformExtension> {
        targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
          compileSdk = liquidVersions.compileSdk.get()
          minSdk = liquidVersions.minSdk.get()

          compilations.withType(KotlinMultiplatformAndroidDeviceTestCompilation::class.java).configureEach {
            targetSdk { version = release(liquidVersions.compileSdk.get()) }
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
          }

          compilations.withType(KotlinMultiplatformAndroidHostTestCompilation::class.java).configureEach {
            isIncludeAndroidResources = true
          }

          packaging {
            resources {
              excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
          }

          compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(liquidVersions.jdk.get().toString()))
          }
        }
      }
    }
  }
}
