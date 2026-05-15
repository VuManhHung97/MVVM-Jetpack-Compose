plugins {
  id(libs.plugins.android.feature.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.search"
}

dependencies {
  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  api(projects.core.domain)

  // Libraries
  api(libs.kotlinx.collections.immutable)
}
