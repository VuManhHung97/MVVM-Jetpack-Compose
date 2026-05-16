package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.response.search.SearchResultResponse

interface SearchRemoteDataSource {
  suspend fun getSearchAutocomplete(keyword: String, limit: Int): Result<List<String>, AppError.ApiException>

  suspend fun getSearchHistory(keyword: String, limit: Int): Result<List<String>, AppError.ApiException>

  suspend fun searchByKeyword(
    keyword: String,
    limit: Int,
    offset: Int,
  ): Result<List<SearchResultResponse>, AppError.ApiException>

  suspend fun deleteSearchHistory(keyword: String): Result<Unit, AppError.ApiException>
}
