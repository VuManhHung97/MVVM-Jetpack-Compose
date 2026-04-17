package com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl

import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UserRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.di.RemoteErrorMapper
import com.vmh.mvvmjetpackcompose.core.network.remote.di.catchingApiException
import com.vmh.mvvmjetpackcompose.core.network.remote.service.UserApiService
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class RealUserRemoteDataSourceImpl @Inject constructor(
  private val userApiService: UserApiService,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val remoteErrorMapper: RemoteErrorMapper,
) : UserRemoteDataSource {
  override suspend fun getProfile() = withContext(appCoroutineDispatchers.io) {
    catchingApiException(remoteErrorMapper) {
      userApiService.getProfile().data
    }
  }
}
