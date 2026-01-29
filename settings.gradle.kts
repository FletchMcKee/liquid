// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
rootProject.name = "liquid-root"

pluginManagement {
  includeBuild("build-logic")
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(":liquid")
include(":benchmark")
include(":core:testing")
include(":samples:android")
include(":samples:jvm")
include(":samples:shared")
include(":samples:web")
