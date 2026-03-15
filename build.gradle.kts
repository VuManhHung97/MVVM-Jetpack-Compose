import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.kotlin.dsl.configure

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false
  alias(libs.plugins.jetbrains.kotlin.jvm) apply false
  alias(libs.plugins.jetbrains.kotlin.kapt) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.kotlin.ksp) apply false
  alias(libs.plugins.google.services) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.google.protobuf) apply false
  alias(libs.plugins.jvm.library) apply false
}

subprojects {
  apply<io.gitlab.arturbosch.detekt.DetektPlugin>()
  configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    parallel = true
    source.from(files("src/"))
    config.from(files("${project.rootDir}/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = true
  }
  dependencies {
    "detektPlugins"(rootProject.libs.compose.rules.detekt)
  }
}

allprojects {
  apply<SpotlessPlugin>()

  configure<SpotlessExtension> {
    val ktlintVersion = rootProject.libs.versions.ktlint.get()

    kotlin {
      target("**/*.kt")
      targetExclude(
        "**/Res.kt", // Compose Multiplatform Res class
        "**/build/**/*.kt", // Kotlin generated files
      )

      ktlint(ktlintVersion)

      trimTrailingWhitespace()
      leadingTabsToSpaces()
      endWithNewline()
    }

    format("xml") {
      target("**/res/**/*.xml")
      targetExclude("**/build/**/*.xml")

      trimTrailingWhitespace()
      leadingTabsToSpaces()
      endWithNewline()
    }

    kotlinGradle {
      target("**/*.gradle.kts", "*.gradle.kts")
      targetExclude("**/build/**/*.kts")

      ktlint(ktlintVersion)

      trimTrailingWhitespace()
      leadingTabsToSpaces()
      endWithNewline()
    }
  }
}
