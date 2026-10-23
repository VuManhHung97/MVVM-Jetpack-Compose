plugins {
  id(libs.plugins.android.feature.impl.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.profile"
}

dependencies {
  api(projects.feature.profile.api)

  implementation(projects.core.ui)
  implementation(projects.core.domain)
  implementation(projects.core.common)
  implementation(projects.core.resource)

  implementation(projects.feature.language.api)
  implementation(projects.feature.webview.api)
  implementation(projects.feature.authentication.api)

  // Libraries
  implementation(libs.kotlinx.collections.immutable)
}
