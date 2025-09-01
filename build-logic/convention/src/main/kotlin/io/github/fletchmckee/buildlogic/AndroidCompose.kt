// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
  commonExtension.apply {
    buildFeatures {
      compose = true
    }

    dependencies {
      val bom = libs.findLibrary("compose-bom").get()
      "implementation"(platform(bom))
      "implementation"(libs.findLibrary("compose-tooling-preview").get())
      "debugImplementation"(libs.findLibrary("compose-tooling").get())
      "debugImplementation"(libs.findLibrary("compose-test-manifest").get())
      "androidTestImplementation"(platform(bom))
      "androidTestImplementation"(libs.findLibrary("compose-junit4").get())
      "testImplementation"(libs.findLibrary("compose-junit4").get())
    }
  }

  @Suppress("UnstableApiUsage")
  extensions.configure<ComposeCompilerGradlePluginExtension> {
    fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }

    fun Provider<*>.relativeToRootProject(dir: String) = map {
      isolated.rootProject.projectDirectory
        .dir("build")
        .dir(projectDir.toRelativeString(rootDir))
    }.map { it.dir(dir) }

    project.providers
      .gradleProperty("enableComposeCompilerMetrics")
      .onlyIfTrue()
      .relativeToRootProject("compose-metrics")
      .let(metricsDestination::set)

    project.providers
      .gradleProperty("enableComposeCompilerReports")
      .onlyIfTrue()
      .relativeToRootProject("compose-reports")
      .let(reportsDestination::set)

    stabilityConfigurationFiles.add(isolated.rootProject.projectDirectory.file("compose_stability.conf"))
  }
}
