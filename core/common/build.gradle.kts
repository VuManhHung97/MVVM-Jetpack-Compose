plugins {
  id(libs.plugins.android.library.core.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.common"
}

dependencies {
  // AndroidX
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)

  // Core modules
  implementation(projects.core.model)
  implementation(projects.core.resource)

  // Kotlinx Collections Immutable
  api(libs.kotlinx.collections.immutable)

  // Moshi
  api(libs.moshi)
  implementation(libs.moshix.sealed.reflect)
  implementation(libs.moshix.sealed.runtime)

  // Kotlinx Coroutines
  api(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.coroutines.android)

  // FlowExt
  api(projects.library.flowext)
}
