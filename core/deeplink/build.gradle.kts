import com.google.devtools.ksp.KspExperimental

plugins {
  id(libs.plugins.android.library.core.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
  id(libs.plugins.kotlin.ksp.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.deeplink"
}

ksp {
  // Disable ksp2 due to issue: https://github.com/airbnb/DeepLinkDispatch/issues/369
  @OptIn(KspExperimental::class)
  useKsp2 = false
  arg("deepLink.incremental", "true")
}

dependencies {
  implementation(libs.deeplinkdispatch)
  ksp(libs.deeplinkdispatch.processor)
}
