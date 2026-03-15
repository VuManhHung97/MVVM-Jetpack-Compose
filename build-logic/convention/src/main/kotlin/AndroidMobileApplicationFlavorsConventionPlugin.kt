import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.vmh.mvvmjetpackcompose.mobile.configureBuildConfigFieldsForMobileApplication
import com.vmh.mvvmjetpackcompose.mobile.configureFlavorsForMobile
import com.vmh.mvvmjetpackcompose.mobile.configureManifestPlaceHolderFieldsForMobileApplication
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidMobileApplicationFlavorsConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      extensions.configure<ApplicationExtension> {
        configureFlavorsForMobile(project = target, shouldCreateFlavors = true)
      }

      configureBuildConfigFieldsForMobileApplication(
        extension = extensions.getByType<ApplicationAndroidComponentsExtension>(),
        keys = listOf(
          "BASE_URL",
          "WEB_VIEW_BASE_URL",
        ),
      )

      configureManifestPlaceHolderFieldsForMobileApplication(
        extension = extensions.getByType<ApplicationAndroidComponentsExtension>(),
        keys = listOf("AD_APPLICATION_ID"),
      )
    }
  }
}
