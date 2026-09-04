// https://docs.gradle.org/current/userguide/declaring_dependencies_basics.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  includeBuild("build-logic")
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://androidx.dev/storage/compose-compiler/repository/")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://jitpack.io")
  }
}

rootProject.name = "MVVMJetpackCompose"
include(":app")

// ---------------------------------------- Core modules ----------------------------------------

include(":core:ui")
include(":core:resource")
include(":core:common")
include(":core:model")
include(":core:domain")
include(":core:network")
include(":core:data")
include(":core:local")
include(":core:domain")
include(":core:deeplink")
include(":core:analytics")
include(":core:notification")
include(":core:navigation")

// ---------------------------------------- Library modules ----------------------------------------

include(":library:flowext")

// ---------------------------------------- Feature modules ----------------------------------------

include(":feature:main:api")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:profile:api")
include(":feature:profile:impl")
include(":feature:authentication:api")
include(":feature:authentication:impl")
include(":feature:webview:api")
include(":feature:webview:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:language:api")
include(":feature:language:impl")

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version ("0.9.0")
}
