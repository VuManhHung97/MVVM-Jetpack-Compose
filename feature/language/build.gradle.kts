plugins {
  id(libs.plugins.android.feature.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.language"
}

dependencies {
  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.model)
}
