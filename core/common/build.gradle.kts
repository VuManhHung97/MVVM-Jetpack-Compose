plugins {
  id(libs.plugins.android.core.get().pluginId)
  id(libs.plugins.android.library.compose.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.common"
}

dependencies {
  // AndroidX
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)

  // Moshi
  api(libs.moshi)
  implementation(libs.moshix.sealed.reflect)
  implementation(libs.moshix.sealed.runtime)

  // Core modules
  implementation(projects.core.resource)

  // Kotlinx Collections Immutable
  api(libs.kotlinx.collections.immutable)

  // Kotlinx Coroutines
  api(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.coroutines.android)

  // FlowExt
  api(projects.library.flowext)
}
