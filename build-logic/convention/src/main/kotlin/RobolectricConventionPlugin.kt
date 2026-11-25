import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.process.CommandLineArgumentProvider

@Suppress("Unused", "UnstableApiUsage")
class RobolectricConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    val robolectricJars = objects.fileCollection()

    mapOf(
      31 to "12-robolectric-7732740-i7",
      36 to "16-robolectric-13921718-2-i7",
    ).forEach { (sdk, version) ->
      val robolectricSdk = configurations.dependencyScope("robolectricSdk$sdk") {
        dependencies.add(project.dependencies.create("org.robolectric:android-all-instrumented:$version"))
      }

      robolectricJars.from(
        configurations.resolvable("robolectricSdk${sdk}Jar") {
          extendsFrom(robolectricSdk)
        },
      )
    }

    val syncRobolectricJars = tasks.register<Sync>("syncRobolectricJars") {
      description = "Syncs Robolectric android-all-instrumented jars into a local directory."
      from(robolectricJars)
      destinationDirectory.set(layout.buildDirectory.dir("preinstrumented"))
    }

    val robolectricDependencyDir = object : CommandLineArgumentProvider {
      @get:InputDirectory
      @get:PathSensitive(PathSensitivity.RELATIVE)
      val directory: Provider<Directory> = syncRobolectricJars.flatMap { it.destinationDirectory }

      override fun asArguments() = listOf("-Drobolectric.dependency.dir=${directory.get().asFile}")
    }

    pluginManager.withPlugin("com.android.kotlin.multiplatform.library") {
      extensions.getByType<KotlinMultiplatformAndroidComponentsExtension>().onVariants { variant ->
        variant.hostTests.values.forEach { hostTest ->
          hostTest.configureTestTask { it.jvmArgumentProviders.add(robolectricDependencyDir) }
        }
      }
    }
  }
}
