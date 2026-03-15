plugins {
  id(libs.plugins.android.core.get().pluginId)
  id(libs.plugins.android.library.compose.get().pluginId)
}

android {
  namespace = "com.vmh.core.ui"
}

dependencies {
  // Libs
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.core.ktx)
  implementation(libs.material)
  implementation(libs.androidx.media3.ui)
  implementation(libs.androidx.media3.common)
  api(libs.moshi)
  implementation(libs.timber)
}
