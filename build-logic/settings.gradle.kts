// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
rootProject.name = "build-logic"

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
  }
  versionCatalogs {
    register("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}

include(":convention")
