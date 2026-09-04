plugins {
  id(libs.plugins.android.feature.impl.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.authentication"
}

dependencies {
  api(projects.feature.authentication.api)

  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  implementation(projects.core.analytics)
  implementation(projects.feature.main.api)

  implementation(projects.core.domain)
  implementation(projects.core.model)
}
