import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_11)
    languageVersion.set(KotlinVersion.KOTLIN_2_0)
  }
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("androidMobileApplication") {
      id = libs.plugins.android.application.core.mobile.get().pluginId
      implementationClass = "AndroidMobileApplicationConventionPlugin"
    }

    register("androidLibrary") {
      id = libs.plugins.android.library.core.get().pluginId
      implementationClass = "AndroidLibraryConventionPlugin"
    }

    register("androidFeature") {
      id = libs.plugins.android.feature.get().pluginId
      implementationClass = "AndroidFeatureConventionPlugin"
    }

    register("androidCore") {
      id = libs.plugins.android.core.get().pluginId
      implementationClass = "AndroidCoreConventionPlugin"
    }

    register("androidHilt") {
      id = libs.plugins.android.hilt.get().pluginId
      implementationClass = "HiltConventionPlugin"
    }

    register("androidLibraryCompose") {
      id = libs.plugins.android.library.compose.get().pluginId
      implementationClass = "AndroidLibraryComposeConventionPlugin"
    }

    register("androidApplicationCompose") {
      id = libs.plugins.android.application.compose.get().pluginId
      implementationClass = "AndroidApplicationComposeConventionPlugin"
    }

    register("androidMobileApplicationFlavors") {
      id = libs.plugins.android.application.flavors.mobile.get().pluginId
      implementationClass = "AndroidMobileApplicationFlavorsConventionPlugin"
    }

    register("androidLibraryFlavors") {
      id = libs.plugins.android.library.flavors.get().pluginId
      implementationClass = "AndroidLibraryFlavorsConventionPlugin"
    }

    register("jvmLibrary") {
      id = libs.plugins.jvm.library.get().pluginId
      implementationClass = "JvmLibraryConventionPlugin"
    }
  }
}
