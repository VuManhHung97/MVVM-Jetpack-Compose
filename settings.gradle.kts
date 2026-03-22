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

// ---------------------------------------- Library modules ----------------------------------------

include(":library:flowext")

// ---------------------------------------- Feature modules ----------------------------------------

include(":feature:main")
include(":feature:home")
include(":feature:profile")
include(":feature:authentication")
plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version ("0.9.0")
}
