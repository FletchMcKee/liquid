// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.buildlogic

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

internal fun Project.configureSpotless() {
  apply(plugin = "com.diffplug.spotless")
  spotless {
    val composeRulesDep = libs.findLibrary("ktlint-compose-rules").get()
    val composeRulesCoordinates = "${composeRulesDep.get().module}:${composeRulesDep.get().version}"

    kotlin {
      target("src/**/*.kt")
      ktlint(libs.findVersion("ktlint").get().requiredVersion)
        .editorConfigOverride(
          mapOf(
            "ktlint_standard_filename" to "disabled",
            "ktlint_standard_property-naming" to "disabled",
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
          ),
        )
        .customRuleSets(listOf(composeRulesCoordinates))
      licenseHeaderFile(rootProject.file("spotless/copyright.txt"))
      endWithNewline()
    }

    kotlinGradle {
      target("*.kts")
      ktlint(libs.findVersion("ktlint").get().requiredVersion)
      licenseHeaderFile(rootProject.file("spotless/copyright.txt"), "(^(?![\\/ ]\\**).*$)")
      endWithNewline()
    }
  }
}

internal fun Project.configureSpotlessRoot() {
  apply(plugin = "com.diffplug.spotless")
  spotless {
    kotlin {
      target("build-logic/convention/src/**/*.kt")
      ktlint(libs.findVersion("ktlint").get().requiredVersion)
        .editorConfigOverride(
          mapOf(
            "ktlint_standard_filename" to "disabled",
            "ktlint_standard_property-naming" to "disabled",
          ),
        )
      licenseHeaderFile(rootProject.file("spotless/copyright.txt"))
      endWithNewline()
    }

    kotlinGradle {
      target("*.kts")
      target("build-logic/*.kts")
      target("build-logic/convention/*.kts")
      ktlint(libs.findVersion("ktlint").get().requiredVersion)
      licenseHeaderFile(rootProject.file("spotless/copyright.txt"), "(^(?![\\/ ]\\**).*$)")
      endWithNewline()
    }
  }
}

private fun Project.spotless(action: SpotlessExtension.() -> Unit) = extensions.configure<SpotlessExtension>(action)
