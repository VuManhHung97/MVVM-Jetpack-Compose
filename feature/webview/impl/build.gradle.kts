plugins {
  id(libs.plugins.android.feature.impl.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.webview"
}

dependencies {
  api(projects.feature.webview.api)

  implementation(projects.core.ui)
  implementation(projects.core.resource)
}
