// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import io.github.fletchmckee.buildlogic.Versions
import io.github.fletchmckee.buildlogic.configureSpotless
import io.github.fletchmckee.buildlogic.configureTesting
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("unused") // Invoked reflectively
class KotlinMultiplatformConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.kotlin.multiplatform")

    configureTesting()

    tasks.withType<JavaCompile>().configureEach {
      sourceCompatibility = Versions.Java.toString()
      targetCompatibility = Versions.Java.toString()
    }

    kotlin {
      applyDefaultHierarchyTemplate()

      compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
      }

      targets.withType<KotlinJvmTarget>().configureEach {
        compilerOptions {
          jvmTarget.set(Versions.Jvm)
        }
      }
    }

    configureSpotless()
  }
}

fun KotlinMultiplatformExtension.addDefaultLiquidTargets() {
  jvm()

  iosArm64()
  iosSimulatorArm64()

  macosArm64()

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    browser()
  }

  js { browser() }
}

internal fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
  extensions.configure<KotlinMultiplatformExtension>(action)
}

internal val Project.kotlin: KotlinMultiplatformExtension
  get() = extensions.getByType<KotlinMultiplatformExtension>()
