package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError

interface SearchRemoteDataSource {
  suspend fun getSearchAutocomplete(keyword: String, limit: Int): Result<List<String>, AppError.ApiException>

  suspend fun getSearchHistory(keyword: String, limit: Int): Result<List<String>, AppError.ApiException>
}
