import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create

@Suppress("unused") // Invoked reflectively
class LiquidSettingsPlugin : Plugin<Settings> {
  override fun apply(settings: Settings) = with(settings) {
    val defaults = extensions.create<LiquidVersions>("liquidVersions")
    val jdkVersion = defaults.jdk
    val minSdkVersion = defaults.minSdk
    val compileSdkVersion = defaults.compileSdk

    @Suppress("UnstableApiUsage")
    gradle.lifecycle.beforeProject {
      extensions.create<LiquidVersions>("liquidSettings").apply {
        jdk.set(jdkVersion)
        minSdk.set(minSdkVersion)
        compileSdk.set(compileSdkVersion)
      }
    }
  }
}

interface LiquidVersions {
  val jdk: Property<Int>
  val minSdk: Property<Int>
  val compileSdk: Property<Int>
}
