import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import com.vmh.mvvmjetpackcompose.mobile.configureFlavorsForMobile
import com.vmh.mvvmjetpackcompose.mobile.createFlavorsForMobile

interface AndroidLibraryFlavorsConventionPluginExtension {
  val shouldCreateFlavors: Property<Boolean>
}

class AndroidLibraryFlavorsConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      extensions.configure<LibraryExtension> {
        configureFlavorsForMobile(project = target, shouldCreateFlavors = false)
      }

      val flavorsExtension = extensions.create(
        "androidLibraryFlavors",
        AndroidLibraryFlavorsConventionPluginExtension::class.java,
      )
      val libraryAndroidComponentsExtension = extensions.getByType<LibraryAndroidComponentsExtension>()

      libraryAndroidComponentsExtension.finalizeDsl { _ ->
        flavorsExtension.shouldCreateFlavors.getOrElse(false)
          .takeIf { it }
          ?.let {
            extensions.configure<LibraryExtension> {
              createFlavorsForMobile()
            }
          }
      }
    }
  }
}
