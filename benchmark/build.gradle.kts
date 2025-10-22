// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.kotlin.dsl.create

plugins {
  alias(libs.plugins.liquid.android.test)
}

@Suppress("UnstableApiUsage")
android {
  namespace = "io.github.fletchmckee.liquid.benchmark"

  defaultConfig {
    testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
  }

  buildTypes {
    create("benchmark") {
      isDebuggable = false
      signingConfig = getByName("debug").signingConfig
      matchingFallbacks += listOf("release")
    }
  }

  targetProjectPath = ":samples:composeApp"
  experimentalProperties["android.experimental.self-instrumenting"] = true

  testOptions.managedDevices.allDevices {
    create<ManagedVirtualDevice>("pixel5Api34") {
      device = "Pixel 5"
      apiLevel = 34
      systemImageSource = "aosp"
    }
  }
}

dependencies {
  implementation(libs.androidx.junit)
  implementation(libs.androidx.espresso.core)
  implementation(libs.androidx.uiautomator)
  implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents {
  beforeVariants(selector().all()) {
    it.enable = it.buildType == "benchmark"
  }
}
