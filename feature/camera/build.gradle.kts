plugins {
  id(libs.plugins.android.feature.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.camera"
}

dependencies {
  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  implementation(projects.core.model)

  implementation(libs.androidx.camera.core)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)

  implementation(libs.kotlin.coil.compose)
}
