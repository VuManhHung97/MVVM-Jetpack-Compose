package com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl

import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.SearchRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.di.RemoteErrorMapper
import com.vmh.mvvmjetpackcompose.core.network.remote.di.catchingApiException
import com.vmh.mvvmjetpackcompose.core.network.remote.service.SearchApiService
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class RealSearchRemoteDataSourceImpl @Inject constructor(
  private val searchApiService: SearchApiService,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val remoteErrorMapper: RemoteErrorMapper,
) : SearchRemoteDataSource {
  override suspend fun getSearchAutocomplete(keyword: String, limit: Int) = withContext(appCoroutineDispatchers.io) {
    catchingApiException(remoteErrorMapper) {
      searchApiService.getSearchAutocomplete(
        keyword = keyword,
        limit = limit,
      ).data
    }
  }

  override suspend fun getSearchHistory(keyword: String, limit: Int) = withContext(appCoroutineDispatchers.io) {
    catchingApiException(remoteErrorMapper) {
      searchApiService.getSearchHistory(
        keyword = keyword,
        limit = limit,
      ).data
    }
  }
}
