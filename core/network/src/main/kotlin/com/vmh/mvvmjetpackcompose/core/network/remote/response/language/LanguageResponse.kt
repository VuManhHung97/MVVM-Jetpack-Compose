package com.vmh.mvvmjetpackcompose.core.network.remote.response.language

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class LanguageResponse(
  @param:Json(name = "id") val id: Long,
  @param:Json(name = "original_name") val originalName: String,
  @param:Json(name = "language_code") val languageCode: String,
)
