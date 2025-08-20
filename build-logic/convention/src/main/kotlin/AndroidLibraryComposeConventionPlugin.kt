import com.android.build.gradle.LibraryExtension
import io.github.fletchmckee.buildlogic.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

@Suppress("unused") // Invoked reflectively
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      apply(plugin = "com.android.library")
      apply(plugin = "org.jetbrains.kotlin.plugin.compose")

      val extension = extensions.getByType<LibraryExtension>()
      configureAndroidCompose(extension)
    }
}
