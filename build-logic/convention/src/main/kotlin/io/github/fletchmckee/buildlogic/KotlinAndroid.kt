package io.github.fletchmckee.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
  commonExtension.apply {
    compileSdk = 36

    defaultConfig {
      minSdk = 26
    }

    compileOptions {
      // Up to Java 11 APIs are available through desugaring
      // https://developer.android.com/studio/write/java11-minimal-support-table
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
      isCoreLibraryDesugaringEnabled = true
    }
  }

  configure<KotlinAndroidProjectExtension> {
    val warningsAsErrors =
      providers
        .gradleProperty("warningsAsErrors")
        .map {
          it.toBoolean()
        }.orElse(false)

    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
      allWarningsAsErrors = warningsAsErrors
      freeCompilerArgs.addAll(
        listOf(
          // Enable experimental coroutines APIs, including Flow
          "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        )
      )
    }
  }

  dependencies {
    "coreLibraryDesugaring"(libs.findLibrary("android.desugarJdkLibs").get())
  }
}
