package com.vmh.mvvmjetpackcompose.core.domain.repository

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError

interface SearchRepository {
  suspend fun getSearchAutocomplete(keyword: String, limit: Int): Result<List<String>, AppError>

  suspend fun getSearchHistory(keyword: String, limit: Int): Result<List<String>, AppError>
}
