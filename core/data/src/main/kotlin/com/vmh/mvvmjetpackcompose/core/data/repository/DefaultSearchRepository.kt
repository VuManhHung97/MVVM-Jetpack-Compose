package com.vmh.mvvmjetpackcompose.core.data.repository

import com.github.michaelbull.result.map
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.data.mapper.search.toSearchResult
import com.vmh.mvvmjetpackcompose.core.domain.repository.SearchRepository
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.SearchRemoteDataSource
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class DefaultSearchRepository @Inject constructor(
  private val searchRemoteDataSource: SearchRemoteDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : SearchRepository {
  override suspend fun getSearchAutocomplete(keyword: String, limit: Int) = withContext(appCoroutineDispatchers.io) {
    searchRemoteDataSource.getSearchAutocomplete(keyword = keyword, limit = limit)
  }

  override suspend fun getSearchHistory(keyword: String, limit: Int) = withContext(appCoroutineDispatchers.io) {
    searchRemoteDataSource.getSearchHistory(keyword = keyword, limit = limit)
  }

  override suspend fun searchByKeyword(keyword: String, limit: Int, offset: Int) =
    withContext(appCoroutineDispatchers.io) {
      searchRemoteDataSource.searchByKeyword(
        keyword = keyword,
        limit = limit,
        offset = offset,
      ).map { items -> items.map { it.toSearchResult() } }
    }
}
