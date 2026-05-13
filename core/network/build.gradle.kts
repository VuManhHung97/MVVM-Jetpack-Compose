plugins {
  id(libs.plugins.android.library.core.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.network"
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.retrofit)
  implementation(libs.retrofit.moshi)
  implementation(libs.moshi)
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging.interceptor)
  implementation(libs.datastore)
  implementation(libs.moshix.sealed.runtime)

  implementation(projects.core.model)
  implementation(projects.core.common)
  implementation(projects.core.local)
}
