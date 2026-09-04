plugins {
  id(libs.plugins.android.feature.impl.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.home"
}

dependencies {
  api(projects.feature.home.api)

  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.feature.search.api)
}
