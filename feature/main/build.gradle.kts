plugins {
  id(libs.plugins.android.feature.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.main"
}

dependencies {
  implementation(projects.core.resource)
  implementation(projects.core.ui)

  implementation(projects.feature.dashboard)
  implementation(projects.feature.account)
  implementation(projects.feature.transaction)
  implementation(projects.feature.profile)

  api(libs.kotlinx.collections.immutable)
}
