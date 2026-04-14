plugins {
  alias(libs.plugins.android.library.core)
  alias(libs.plugins.android.hilt)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.data"
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.kotlinx.coroutines.core)

  implementation(projects.core.common)
  implementation(projects.core.network)
  implementation(projects.core.local)
  implementation(projects.core.domain)
  implementation(libs.kotlin.result)
}
