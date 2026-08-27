plugins {
  id(libs.plugins.android.library.core.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.navigation"
}

dependencies {
  api(libs.androidx.navigation3.runtime)
  api(libs.androidx.lifecycle.viewmodel.navigation3)
  api(libs.kotlinx.serialization.core)
  api(libs.kotlinx.collections.immutable)
}
