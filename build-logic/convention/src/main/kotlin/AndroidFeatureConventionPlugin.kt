import extensions.implementation
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply {
        apply("android.library")
        apply("android.hilt")
        apply("android.library.flavors")
      }

      dependencies {
        implementation(libs.findLibrary("androidx.core.ktx").get())
        implementation(libs.findLibrary("androidx.activity").get())
        implementation(libs.findLibrary("androidx.appcompat").get())
      }
    }
  }
}
