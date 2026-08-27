plugins {
  id(libs.plugins.android.feature.api.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.authentication.api"
}
dependencies {
  implementation(libs.androidx.navigation.common.ktx)
}
