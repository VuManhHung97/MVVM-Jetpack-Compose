plugins {
  id(libs.plugins.android.library.core.get().pluginId)
  id(libs.plugins.android.hilt.get().pluginId)
  id(libs.plugins.google.protobuf.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.core.local"
}

dependencies {
  // Datastore
  implementation(libs.datastore)
  implementation(libs.moshi)
  // Protobuf
  api(libs.protobuf.javalite)

  // Modules
  api(projects.core.model)
  implementation(projects.core.common)
}

protobuf {
  protoc {
    artifact = libs.protoc.get().toString()
  }

  generateProtoTasks {
    all().forEach { task ->
      task.builtins {
        register("java") {
          option("lite")
        }
      }
    }
  }
}
