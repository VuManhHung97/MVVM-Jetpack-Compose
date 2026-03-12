package com.vmh.mvvmjetpackcompose.mobile

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.BuildConfigField
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.Variant
import java.util.Properties
import org.gradle.api.Project
import com.vmh.mvvmjetpackcompose.utils.isCiBuild
import com.vmh.mvvmjetpackcompose.utils.readPropertiesFile

internal fun Project.configureBuildConfigFieldsForMobileApplication(
  extension: ApplicationAndroidComponentsExtension,
  keys: List<String>,
) {
  extension.onVariants { variant ->
    variant.configureBuildConfigFromProperties(this, keys)
  }
}

internal fun Project.configureManifestPlaceHolderFieldsForMobileApplication(
  extension: ApplicationAndroidComponentsExtension,
  keys: List<String>,
) {
  extension.onVariants { variant ->
    variant.configureManifestPlaceHolderFromProperties(this, keys)
  }
}

internal fun Project.configureBuildConfigFieldsForMobileFeatureLibrary(
  extension: LibraryAndroidComponentsExtension,
  keys: List<String>,
) {
  extension.onVariants { variant ->
    variant.configureBuildConfigFromProperties(this, keys)
  }
}

internal fun Project.configureManifestPlaceHolderFieldsForMobileFeatureLibrary(
  extension: LibraryAndroidComponentsExtension,
  keys: List<String>,
) {
  extension.onVariants { variant ->
    variant.configureManifestPlaceHolderFromProperties(this, keys)
  }
}

private fun Variant.configureManifestPlaceHolderFromProperties(project: Project, keys: List<String>) {
  val props = getProperties(project)
  keys.forEach { key ->
    props[key]
      ?.let { value -> manifestPlaceholders.put(key, value.toString()) }
      ?: println("Property '$key' not found in provided properties.")
  }
}

private fun Variant.configureBuildConfigFromProperties(project: Project, keys: List<String>) {
  val props = getProperties(project)
  keys.forEach { key ->
    props[key]
      ?.let { value ->
        buildConfigFields!!.put(
          key,
          BuildConfigField(
            type = "String",
            value = "\"$value\"",
            comment = null,
          ),
        )
      }
      ?: println("Property '$key' not found in provided properties.")
  }
}

private fun Variant.getProperties(project: Project): Properties {
  // Convert productFlavors into a map of Dimension -> Flavor Name
  val flavorsByDimension = productFlavors.toMap()

  val appFlavor = flavorsByDimension[MobileFlavorDimension.product.name]
    ?.let(MobileFlavor::from)
    ?: MobileFlavor.base

  val environment = flavorsByDimension[MobileFlavorDimension.environment.name]
    ?.let(MobileEnvironment::from)
    ?: MobileEnvironment.dev

  val environmentDir = when (environment) {
    MobileEnvironment.dev -> "dev"
    MobileEnvironment.prod -> "prod"
  }

  val propertiesFilePathFromRootProject = when {
    project.isCiBuild -> ".github/ci.properties"

    else -> when (appFlavor) {
      MobileFlavor.base -> "properties/$environmentDir/base.properties"
    }
  }

  return project.readPropertiesFile(propertiesFilePathFromRootProject)
}
