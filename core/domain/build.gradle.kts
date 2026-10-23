plugins {
  alias(libs.plugins.jvm.library)
}

dependencies {
  api(projects.core.model)

  // Types exposed in repository signatures (`Result`, `Flow`)
  api(libs.kotlin.result)
  api(libs.kotlinx.coroutines.core)
}
