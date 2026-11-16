// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import io.github.fletchmckee.buildlogic.configureSpotless
import io.github.fletchmckee.buildlogic.configureTesting
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("unused") // Invoked reflectively
class KotlinMultiplatformConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.kotlin.multiplatform")
    val liquidVersions = the<LiquidVersions>()

    kotlin {
      applyDefaultHierarchyTemplate()

      compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
      }

      targets.withType<KotlinJvmTarget>().configureEach {
        compilerOptions {
          jvmTarget.set(JvmTarget.fromTarget(liquidVersions.jdk.get().toString()))
        }
      }
    }

    configureSpotless()
    configureTesting()
  }
}

internal fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
  extensions.configure<KotlinMultiplatformExtension>(action)
}
