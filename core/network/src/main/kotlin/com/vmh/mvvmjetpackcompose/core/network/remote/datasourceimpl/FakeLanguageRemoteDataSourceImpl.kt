package com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl

import com.github.michaelbull.result.Ok
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.LanguageRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.response.language.LanguageResponse
import javax.inject.Inject

internal class FakeLanguageRemoteDataSourceImpl @Inject constructor() : LanguageRemoteDataSource {
  override suspend fun getLanguages() = Ok(
    listOf(
      LanguageResponse(id = 1L, originalName = "English", languageCode = "en"),
      LanguageResponse(id = 2L, originalName = "日本語", languageCode = "ja"),
    ),
  )
}
