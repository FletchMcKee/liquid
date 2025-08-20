// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import io.github.fletchmckee.buildlogic.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("Unused") // Invoked reflectively
class RootConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    configureSpotless()
  }
}
