import com.vmh.mvvmjetpackcompose.mobile.MobileFlavor
import java.text.SimpleDateFormat
import java.util.Date

plugins {
  id(libs.plugins.android.application.core.mobile.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose"

  defaultConfig {
    applicationId = MobileFlavor.base.applicationId
    versionCode = MobileFlavor.base.versionCode
    versionName = MobileFlavor.base.versionName
  }


//  androidResources {
//    ignoreAssetsPatterns += listOf(
//      "!PublicSuffixDatabase.list", // OkHttp
//      "!composepreference.preference.generated.resources",
//    )
//    generateLocaleConfig = true
//    localeFilters += mutableSetOf(
//      "en",
//      "en-rUS",
//      "en-rGB",
//      "es",
//      "es-rES",
//      "ja",
//      "ja-rJP",
//    )
//  }

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

  implementation(libs.moshi)
  implementation(libs.moshix.sealed.reflect)

  // Core Modules
  implementation(projects.core.ui)
}