plugins {
  alias(libs.plugins.jvm.library)
}

dependencies {
  api(libs.kotlinx.coroutines.core)
}

kotlin {
  sourceSets {
    all {
      languageSettings {
        optIn("com.vmh.mvvmjetpackcompose.library.flowext.DelicateFlowExtApi")
      }
    }
  }

  explicitApi()
}
