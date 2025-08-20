// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
plugins {
  `kotlin-dsl`
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    target("**/*.kt")
    ktlint()
    licenseHeaderFile(rootProject.file("../spotless/copyright.txt"))
  }

  kotlinGradle {
    target("**/*.kts")
    targetExclude("build/**/*.kts")
    ktlint()
    licenseHeaderFile(rootProject.file("../spotless/copyright.txt"), "(^(?![\\/ ]\\**).*$)")
  }
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
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
    register("androidLibrary") {
      id =
        libs.plugins.liquid.android.library
          .asProvider()
          .get()
          .pluginId
      implementationClass = "AndroidLibraryConventionPlugin"
    }
    register("androidLibraryCompose") {
      id =
        libs.plugins.liquid.android.library.compose
          .get()
          .pluginId
      implementationClass = "AndroidLibraryComposeConventionPlugin"
    }
  }
}
