// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import io.github.fletchmckee.buildlogic.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

@Suppress("unused") // Invoked reflectively
class AndroidApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    apply(plugin = "com.android.application")

    configureAndroid()
  }
}
