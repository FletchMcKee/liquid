// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.variant.HasUnitTestBuilder
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import kotlinx.validation.ExperimentalBCVApi
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
  alias(libs.plugins.liquid.android.library)
  alias(libs.plugins.liquid.kotlin.multiplatform)
  alias(libs.plugins.liquid.compose.multiplatform)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.binary.compatibility.validator)
  alias(libs.plugins.dokka)
}

android {
  namespace = "io.github.fletchmckee.liquid"
}

kotlin {
  explicitApi()
  addDefaultLiquidTargets()

  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
  }

  sourceSets {
    commonMain.dependencies {
      api(compose.ui)
      implementation(compose.foundation)
    }

    val skikoMain by creating {
      dependsOn(commonMain.get())
    }

    iosMain {
      dependsOn(skikoMain)
    }

    macosMain {
      dependsOn(skikoMain)
    }

    jvmMain {
      dependsOn(skikoMain)
    }

    wasmJsMain {
      dependsOn(skikoMain)
    }

    jsMain {
      dependsOn(skikoMain)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.assertk)

      @OptIn(ExperimentalComposeLibrary::class)
      implementation(compose.uiTest)
    }

    jvmTest.dependencies {
      implementation(compose.desktop.currentOs)
    }
  }
}

dependencies {
  constraints {
    "androidMainApi"("androidx.compose.ui:ui:1.9.3") {
      because("Library now uses JetBrains Compose Multiplatform")
    }
    "androidMainImplementation"("androidx.compose.foundation:foundation:1.9.3") {
      because("Library now uses JetBrains Compose Multiplatform")
    }
  }

  androidTestImplementation(libs.androidx.junit)
  debugImplementation(libs.compose.test.manifest)
}

androidComponents {
  beforeVariants { variantBuilder ->
    (variantBuilder as? HasUnitTestBuilder)?.apply {
      enableUnitTest = false
    }
  }
}

tasks.withType<KotlinJsTest> {
  enabled = false
}

apiValidation {
  @OptIn(ExperimentalBCVApi::class)
  klib {
    enabled = true
  }
}

mavenPublishing {
  configure(
    KotlinMultiplatform(
      javadocJar = JavadocJar.Dokka("dokkaGenerate"),
      sourcesJar = true,
      androidVariantsToPublish = listOf("release"),
    ),
  )
}
