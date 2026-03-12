package com.vmh.mvvmjetpackcompose.mobile

import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import com.vmh.mvvmjetpackcompose.utils.readPropertiesFile

@Suppress("EnumEntryName")
enum class MobileBuildType {
  debug,
  release,
}

@Suppress("EnumEntryName")
enum class MobileFlavorDimension {
  product,
  environment,
}

@Suppress("EnumEntryName")
enum class MobileEnvironment(val environmentName: String) {
  dev(environmentName = "dev"),
  prod(environmentName = "prod"),
  ;

  companion object {
    @OptIn(ExperimentalStdlibApi::class)
    fun from(flavorName: String): MobileEnvironment? = entries.find { it.environmentName == flavorName }
  }
}

@Suppress("EnumEntryName")
enum class MobileFlavor(
  val flavorName: String,
  val applicationId: String,
  val versionCode: Int,
  val versionName: String,
) {
  base(
    flavorName = "base",
    applicationId = "com.vmh.mvvmjetpackcompose",
    versionCode = 1,
    versionName = "1.0.0",
  ),
  ;

  companion object {
    @OptIn(ExperimentalStdlibApi::class)
    fun from(flavorName: String): MobileFlavor? = entries.find { it.flavorName == flavorName }
  }
}

@OptIn(ExperimentalStdlibApi::class)
internal fun CommonExtension<*, *, *, *, *, *>.configureFlavorsForMobile(
  project: Project,
  shouldCreateFlavors: Boolean,
) {
  if (shouldCreateFlavors) {
    // Configure signing configs only for application modules (as libraries don't support signingConfigs)
    signingConfigs {
      getByName(MobileBuildType.debug.name) {
        val keystoreProperties = project.readPropertiesFile("keystore/debugkey.properties")

        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
        storeFile =
          project.rootProject.file(keystoreProperties["storeFile"] as String).apply { check(exists()) }
        storePassword = keystoreProperties["storePassword"] as String
      }

      // TODO(release): change signing configs
      create(MobileBuildType.release.name) {
        initWith(getByName(MobileBuildType.debug.name))
      }
    }
  }
  buildTypes {
    getByName(MobileBuildType.debug.name) {
      isMinifyEnabled = false

      if (this@configureFlavorsForMobile is ApplicationExtension && this is ApplicationBuildType) {
        isDebuggable = true
      }
    }

    getByName(MobileBuildType.release.name) {
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

      if (this@configureFlavorsForMobile is ApplicationExtension && this is ApplicationBuildType) {
        isMinifyEnabled = true
        isShrinkResources = true
        isDebuggable = false
        signingConfig = signingConfigs.getByName(MobileBuildType.release.name)
      }
    }
  }

  if (!shouldCreateFlavors) return
  createFlavorsForMobile()
}

@OptIn(ExperimentalStdlibApi::class)
internal fun CommonExtension<*, *, *, *, *, *>.createFlavorsForMobile() {
  flavorDimensions += MobileFlavorDimension.entries.map { it.name }

  // Configure product flavors only for application modules (as libraries don't support applicationId, versionCode, etc.)
  productFlavors {
    MobileFlavor.entries.forEach { ottCloudsFlavor ->
      register(ottCloudsFlavor.flavorName) {
        dimension = MobileFlavorDimension.product.name
        if (this@createFlavorsForMobile is ApplicationExtension && this is ApplicationProductFlavor) {
          applicationId = ottCloudsFlavor.applicationId
          versionCode = ottCloudsFlavor.versionCode
          versionName = ottCloudsFlavor.versionName
        }
      }
    }
    MobileEnvironment.entries.forEach { env ->
      register(env.environmentName) {
        dimension = MobileFlavorDimension.environment.name
        if (this@createFlavorsForMobile is ApplicationExtension && this is ApplicationProductFlavor) {
          applicationIdSuffix = if (env == MobileEnvironment.dev) ".${env.environmentName}" else null
        }
      }
    }
  }
}
