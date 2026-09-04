import com.vmh.mvvmjetpackcompose.mobile.MobileFlavor
import java.text.SimpleDateFormat
import java.util.Date

plugins {
  id(libs.plugins.android.application.core.mobile.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose"

  defaultConfig {
    applicationId = MobileFlavor.base.applicationId
    versionCode = MobileFlavor.base.versionCode
    versionName = MobileFlavor.base.versionName
  }

  androidResources {
    ignoreAssetsPatterns += listOf(
      "!PublicSuffixDatabase.list", // OkHttp
      "!composepreference.preference.generated.resources",
    )
    generateLocaleConfig = true
    localeFilters += mutableSetOf(
      "en",
      "en-rUS",
      "en-rGB",
      "ja",
      "ja-rJP",
    )
  }

  buildTypes {
    release {
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }

  applicationVariants.all {
    this.outputs.all {
      val date = Date()
      val dateFormat = SimpleDateFormat("dd-MM HH mm")
      (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
        this.name + "_" + dateFormat.format(date) + ".apk"
    }
  }
}

dependencies {
  // Add library
  implementation(libs.androidx.core.splash)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.timber)
  implementation(libs.kotlin.result)
  implementation(libs.kotlin.result.coroutine)
  implementation(libs.material)
  implementation(libs.kotlinx.collections.immutable)

  implementation(libs.moshi)
  implementation(libs.moshix.sealed.reflect)

  implementation(projects.core.navigation)
  implementation(libs.androidx.navigation3.ui)

  implementation(projects.feature.authentication.api)
  implementation(projects.feature.authentication.impl)
  implementation(projects.feature.main.api)
  implementation(projects.feature.home.api)
  implementation(projects.feature.home.impl)
  implementation(projects.feature.profile.api)
  implementation(projects.feature.profile.impl)
  implementation(projects.feature.webview.api)
  implementation(projects.feature.webview.impl)
  implementation(projects.feature.search.api)
  implementation(projects.feature.search.impl)
  implementation(projects.feature.language.api)
  implementation(projects.feature.language.impl)

  // Core Modules
  implementation(projects.core.ui)
  implementation(projects.core.data)
  implementation(projects.core.domain)
  implementation(projects.core.common)
  implementation(projects.core.resource)
  implementation(projects.core.network)
  implementation(projects.core.local)
  implementation(projects.core.model)
  implementation(projects.core.deeplink)
  implementation(projects.core.analytics)
  implementation(projects.core.notification)
}
