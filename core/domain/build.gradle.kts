plugins {
  id(libs.plugins.android.library.core.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.domain"
}

dependencies {
  api(projects.core.model)
}
