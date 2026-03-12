import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.vmh.mvvmjetpackcompose.configureKotlinAndroid

class AndroidMobileApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("org.jetbrains.kotlin.android")
        apply("kotlin-parcelize")
        apply("android.application.compose")
        apply("android.application.flavors.mobile")
      }

      extensions.configure<ApplicationExtension> {
        buildFeatures {
          viewBinding = true
          buildConfig = true
        }

        configureKotlinAndroid(this)
      }
    }
  }
}
