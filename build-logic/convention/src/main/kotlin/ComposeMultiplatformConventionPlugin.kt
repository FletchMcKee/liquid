// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import io.github.fletchmckee.buildlogic.configureComposeMetrics
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused") // Invoked reflectively
class ComposeMultiplatformConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.compose")
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    configureComposeMetrics()
  }
}
