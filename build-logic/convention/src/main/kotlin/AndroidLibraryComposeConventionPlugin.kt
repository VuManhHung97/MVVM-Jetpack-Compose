import com.android.build.api.dsl.LibraryExtension
import extensions.api
import extensions.implementation
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.library")
        apply("org.jetbrains.kotlin.android")
        apply(libs.findPlugin("compose.compiler").get().get().pluginId)
      }

      extensions.configure<LibraryExtension> {
        buildFeatures {
          compose = true
        }
      }

      dependencies {
        api(platform(libs.findLibrary("androidx.compose.bom").get()))
        implementation(libs.findLibrary("androidx.compose.ui.tooling").get())
        implementation(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
        implementation(libs.findLibrary("androidx.compose.ui").get())
        implementation(libs.findLibrary("androidx.activity.compose").get())
        implementation(libs.findLibrary("androidx.compose.material3").get())
        implementation(libs.findLibrary("androidx.lifecycle.runtime.compose").get())
        implementation(libs.findLibrary("androidx.compose.foundation").get())
        implementation(libs.findLibrary("androidx.compose.runtime").get())
        implementation(libs.findLibrary("androidx.navigation.compose").get())
        implementation(libs.findLibrary("androidx.hilt.navigation.compose").get())
        implementation(libs.findLibrary("kotlin.coil.compose.network").get())
        implementation(libs.findLibrary("kotlin.coil.compose").get())
      }
    }
  }
}
