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
  // Consumed as an infrastructure data source for the device's FCM token (like :core:network/:core:local).
  implementation(projects.core.notification)
  implementation(libs.kotlin.result)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlin.result)
}
