plugins {
  id(libs.plugins.android.feature.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.webview"
}

dependencies {
  implementation(projects.core.ui)
  implementation(projects.core.resource)
}
