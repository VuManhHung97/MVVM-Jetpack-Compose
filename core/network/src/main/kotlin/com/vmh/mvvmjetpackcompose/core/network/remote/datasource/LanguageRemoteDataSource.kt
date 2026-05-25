package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.response.language.LanguageResponse

interface LanguageRemoteDataSource {
  suspend fun getLanguages(): Result<List<LanguageResponse>, AppError.ApiException>
}
