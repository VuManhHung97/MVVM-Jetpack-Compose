plugins {
  id(libs.plugins.android.feature.impl.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.profile"
}

dependencies {
  api(projects.feature.profile.api)

  api(projects.core.ui)
  api(projects.core.domain)
  api(projects.core.common)
  api(projects.core.resource)

  implementation(projects.feature.language.api)
  implementation(projects.feature.webview.api)
  implementation(projects.feature.authentication.api)

  // Libraries
  api(libs.kotlinx.collections.immutable)
}
