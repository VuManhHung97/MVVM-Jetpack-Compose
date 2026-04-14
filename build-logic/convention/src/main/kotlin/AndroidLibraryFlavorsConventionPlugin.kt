import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.vmh.mvvmjetpackcompose.mobile.configureBuildConfigFieldsForMobileFeatureLibrary
import com.vmh.mvvmjetpackcompose.mobile.configureFlavorsForMobile
import com.vmh.mvvmjetpackcompose.mobile.createFlavorsForMobile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

interface AndroidLibraryFlavorsConventionPluginExtension {
  val shouldCreateFlavors: Property<Boolean>
}

class AndroidLibraryFlavorsConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      extensions.configure<LibraryExtension> {
        configureFlavorsForMobile(project = target, shouldCreateFlavors = false)
      }

      configureBuildConfigFieldsForMobileFeatureLibrary(
        extension = extensions.getByType<LibraryAndroidComponentsExtension>(),
        keys = listOf(
          "BASE_URL",
          "WEB_VIEW_BASE_URL",
        ),
      )

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
