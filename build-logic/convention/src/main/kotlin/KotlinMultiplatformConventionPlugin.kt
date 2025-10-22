// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import io.github.fletchmckee.buildlogic.configureSpotless
import io.github.fletchmckee.buildlogic.configureTesting
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("unused") // Invoked reflectively
class KotlinMultiplatformConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.kotlin.multiplatform")

    configureTesting()

    tasks.withType<JavaCompile>().configureEach {
      sourceCompatibility = "11"
      targetCompatibility = "11"
    }

    kotlin {
      applyDefaultHierarchyTemplate()

      compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
      }

      targets.withType<KotlinAndroidTarget> {
        compilerOptions {
          jvmTarget.set(JvmTarget.JVM_11)
        }
      }

      targets.withType<KotlinJvmTarget> {
        compilerOptions {
          jvmTarget.set(JvmTarget.JVM_11)
        }
      }
    }

    configureSpotless()
  }
}

fun KotlinMultiplatformExtension.addDefaultLiquidTargets() {
  jvm()
  androidTarget {
    publishLibraryVariants("release")
  }

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  macosX64()
  macosArm64()

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    browser()
  }

  js(IR) {
    browser()
  }
}

internal fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
  extensions.configure<KotlinMultiplatformExtension>(action)
}

internal val Project.kotlin: KotlinMultiplatformExtension
  get() = extensions.getByType<KotlinMultiplatformExtension>()
