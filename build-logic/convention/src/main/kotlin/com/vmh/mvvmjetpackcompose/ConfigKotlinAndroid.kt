package com.vmh.mvvmjetpackcompose

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import extensions.coreLibraryDesugaring
import extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
  configSdk(commonExtension)

  commonExtension.apply {
    compileOptions {
      isCoreLibraryDesugaringEnabled = true

      // Up to Java 11 APIs are available through desugaring
      // https://developer.android.com/studio/write/java11-minimal-support-table
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
    }
  }

  dependencies {
    coreLibraryDesugaring(libs.findLibrary("desugar.jdk.libs").get())
  }

  configureKotlin<KotlinAndroidProjectExtension>()
}

internal fun configSdk(commonExtension: CommonExtension<*, *, *, *, *, *>) {
  commonExtension.apply {
    compileSdk = 36

    defaultConfig {
      minSdk = 26

      if (this is ApplicationDefaultConfig) {
        targetSdk = 35
      }
    }
  }
}

/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
  extensions.configure<JavaPluginExtension> {
    // Up to Java 11 APIs are available through desugaring
    // https://developer.android.com/studio/write/java11-minimal-support-table
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  configureKotlin<KotlinJvmProjectExtension>()
}

/**
 * Configure base Kotlin options
 */
private inline fun <reified T : KotlinProjectExtension> Project.configureKotlin() = configure<T> {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
    vendor.set(JvmVendorSpec.AZUL)
  }

  val warningsAsErrors: String? by project
  when (this) {
    is KotlinAndroidProjectExtension -> compilerOptions
    is KotlinJvmProjectExtension -> compilerOptions
    else -> throw IllegalStateException("Unsupported kotlin extension ${this::class}")
  }.run {
    jvmTarget.set(JvmTarget.JVM_11)
    allWarningsAsErrors.set(warningsAsErrors.toBoolean())
    freeCompilerArgs.addAll(
      "-opt-in=kotlin.RequiresOptIn",
      "-Xstring-concat=inline",
      "-Xannotation-target-all", // https://kotlinlang.org/docs/whatsnew22.html#all-meta-target-for-properties
    )

    if (project.findProperty("composeCompilerReports") == "true") {
      freeCompilerArgs.addAll(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.layout.buildDirectory}/compose_compiler",
      )
    }

    if (project.findProperty("composeCompilerMetrics") == "true") {
      freeCompilerArgs.addAll(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.layout.buildDirectory}/compose_compiler",
      )
    }
  }
}
