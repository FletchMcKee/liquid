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

plugins {
  id("io.github.fletchmckee.liquidSettings")
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
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")

liquidVersions {
  jdk.set(11)
  minSdk.set(24)
  compileSdk.set(37)
}

include(":liquid")
include(":benchmark")
include(":core:testing")
include(":samples:android")
include(":samples:jvm")
include(":samples:shared")
include(":samples:web")
