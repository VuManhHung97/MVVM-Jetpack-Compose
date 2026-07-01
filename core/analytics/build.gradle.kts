plugins {
  id(libs.plugins.android.library.core.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.analytics"
}

dependencies {
  implementation(libs.androidx.core.ktx)

  // Firebase Analytics — the entire Firebase dependency is encapsulated within this module
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.analytics)
}
