package com.vmh.mvvmjetpackcompose.utils

import java.util.Properties
import org.gradle.api.Project

internal fun Project.readPropertiesFile(filePathFromRoot: String): Properties {
  val properties = Properties()
  val file = rootProject.file(filePathFromRoot)

  if (!file.exists()) {
    throw IllegalArgumentException("Properties file not found: $filePathFromRoot")
  }

  file.inputStream().use { properties.load(it) }
  return properties
}
