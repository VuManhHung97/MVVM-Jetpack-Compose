plugins {
  id(libs.plugins.android.core.get().pluginId)
  id(libs.plugins.android.library.compose.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.resource"
}

dependencies {
  implementation(libs.material)
}
