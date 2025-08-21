// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.dsl.ApplicationExtension
import io.github.fletchmckee.buildlogic.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

@Suppress("unused") // Invoked reflectively
class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    apply(plugin = "com.android.application")
    apply(plugin = "org.jetbrains.kotlin.plugin.compose")

    val extension = extensions.getByType<ApplicationExtension>()
    configureAndroidCompose(extension)
  }
}
