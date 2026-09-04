plugins {
  id(libs.plugins.android.feature.impl.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.search"
}

dependencies {
  api(projects.feature.search.api)

  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  api(projects.core.domain)

  // Libraries
  implementation(libs.androidx.compose.material.iconsCore)
  api(libs.kotlinx.collections.immutable)
}
