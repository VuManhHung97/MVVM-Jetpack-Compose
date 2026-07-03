plugins {
  id(libs.plugins.android.library.core.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.notification"
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.kotlinx.coroutines.android)

  implementation(projects.core.model)
  implementation(projects.core.common)

  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.messaging)
}
