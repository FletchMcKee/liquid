plugins {
  `kotlin-dsl`
}

gradlePlugin {
  plugins {
    register("liquidSettings") {
      id = "io.github.fletchmckee.liquidSettings"
      implementationClass = "LiquidSettingsPlugin"
    }
  }
}
