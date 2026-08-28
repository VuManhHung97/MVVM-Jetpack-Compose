import com.android.build.api.dsl.LibraryExtension
import com.vmh.mvvmjetpackcompose.configureKotlinAndroid
import extensions.api
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureApiConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.library")
        apply("org.jetbrains.kotlin.android")
        apply("kotlin-parcelize")
        apply("org.jetbrains.kotlin.plugin.serialization")
      }

      extensions.configure<LibraryExtension> { configureKotlinAndroid(this) }

      dependencies {
        api(project(":core:navigation"))
      }
    }
  }
}
