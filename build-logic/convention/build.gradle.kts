// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
plugins {
  `kotlin-dsl`
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    target("src/**/*.kt")
    ktlint(
      libs.ktlint.core
        .get()
        .version,
    ).editorConfigOverride(
      mapOf(
        "ktlint_standard_filename" to "disabled",
        "ktlint_standard_property-naming" to "disabled",
      ),
    )
    licenseHeaderFile(rootProject.file("../spotless/copyright.txt"))
  }

  kotlinGradle {
    target("*.kts")
    ktlint()
    licenseHeaderFile(rootProject.file("../spotless/copyright.txt"), "(^(?![\\/ ]\\**).*$)")
  }
}

dependencies {
  compileOnly(libs.android.api.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.compose.gradlePlugin)
  compileOnly(libs.spotless.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("root") {
      id =
        libs.plugins.liquid.root
          .get()
          .pluginId
      implementationClass = "RootConventionPlugin"
    }

    register("kotlinMultiplatform") {
      id =
        libs.plugins.liquid.kotlin.multiplatform
          .get()
          .pluginId
      implementationClass = "KotlinMultiplatformConventionPlugin"
    }

    register("composeMultiplatform") {
      id =
        libs.plugins.liquid.compose.multiplatform
          .get()
          .pluginId
      implementationClass = "ComposeMultiplatformConventionPlugin"
    }

    register("androidMultiplatformLibrary") {
      id =
        libs.plugins.liquid.android.kotlin.multiplatform.library
          .get()
          .pluginId
      implementationClass = "AndroidMultiplatformLibraryConventionPlugin"
    }

    register("androidApplication") {
      id =
        libs.plugins.liquid.android.application
          .get()
          .pluginId
      implementationClass = "AndroidApplicationConventionPlugin"
    }

    register("androidTest") {
      id =
        libs.plugins.liquid.android.test
          .get()
          .pluginId
      implementationClass = "AndroidTestConventionPlugin"
    }
  }
}
