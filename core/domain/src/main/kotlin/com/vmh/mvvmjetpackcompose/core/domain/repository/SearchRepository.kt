package com.vmh.mvvmjetpackcompose.core.domain.repository

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.search.SearchResult

interface SearchRepository {
  suspend fun getSearchAutocomplete(keyword: String, limit: Int): Result<List<String>, AppError>

  suspend fun getSearchHistory(keyword: String, limit: Int): Result<List<String>, AppError>

  suspend fun searchByKeyword(keyword: String, limit: Int, offset: Int): Result<List<SearchResult>, AppError>

  suspend fun deleteSearchHistory(keyword: String): Result<Unit, AppError>
}
