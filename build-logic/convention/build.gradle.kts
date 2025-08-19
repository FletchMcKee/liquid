plugins {
  `kotlin-dsl`
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    target("src/**/*.kt")
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
