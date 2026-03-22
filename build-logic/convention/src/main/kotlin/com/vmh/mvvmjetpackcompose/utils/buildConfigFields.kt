package com.vmh.mvvmjetpackcompose.utils

import com.android.build.api.dsl.BuildType
import java.util.Properties

@Suppress("NOTHING_TO_INLINE")
internal inline fun BuildType.addBuildConfigStringFieldFromProperties(properties: Properties, keys: List<String>) {
  keys.forEach { addBuildConfigStringFieldFromProperties(properties, it) }
}

private fun BuildType.addBuildConfigStringFieldFromProperties(properties: Properties, key: String) {
  properties.getProperty(key)?.let { value ->
    buildConfigField("String", key, "\"$value\"")
  } ?: println("Property '$key' not found in provided properties.")
}
