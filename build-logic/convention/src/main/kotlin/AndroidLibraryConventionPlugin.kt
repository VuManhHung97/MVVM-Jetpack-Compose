import com.android.build.api.dsl.LibraryExtension
import com.vmh.mvvmjetpackcompose.configureKotlinAndroid
import extensions.implementation
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.library")
        apply("org.jetbrains.kotlin.android")
        apply("kotlin-parcelize")
        apply("android.library.compose")
        apply("android.library.flavors")
      }

      extensions.configure<LibraryExtension> {
        buildFeatures {
          viewBinding = true
          buildConfig = true
        }

        configureKotlinAndroid(this)

        dependencies {
          implementation(libs.findLibrary("timber").get())
          implementation(libs.findLibrary("kotlin.result").get())
          implementation(libs.findLibrary("kotlin.result.coroutine").get())
        }
      }
    }
  }
}
