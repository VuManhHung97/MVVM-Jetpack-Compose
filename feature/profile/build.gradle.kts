plugins {
  id(libs.plugins.android.feature.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.profile"
}

dependencies {
  api(projects.core.ui)
  api(projects.core.domain)
  api(projects.core.common)
  api(projects.core.resource)

  // Libraries
  api(libs.kotlinx.collections.immutable)
}
