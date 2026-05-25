package com.vmh.mvvmjetpackcompose.core.data.mapper.language

import com.vmh.mvvmjetpackcompose.core.model.language.Language
import com.vmh.mvvmjetpackcompose.core.network.remote.response.language.LanguageResponse

fun LanguageResponse.toLanguage(): Language = Language(
  id = id,
  originalName = originalName,
  languageCode = Language.LanguageCode.fromTag(languageCode),
)
