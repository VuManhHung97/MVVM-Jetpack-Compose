package com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl

import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.FcmTokenRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.di.RemoteErrorMapper
import com.vmh.mvvmjetpackcompose.core.network.remote.di.catchingApiException
import com.vmh.mvvmjetpackcompose.core.network.remote.request.FcmTokenRequestBody
import com.vmh.mvvmjetpackcompose.core.network.remote.service.FcmTokenApiService
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class RealFcmTokenRemoteDataSourceImpl @Inject constructor(
  private val fcmTokenApiService: FcmTokenApiService,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val remoteErrorMapper: RemoteErrorMapper,
) : FcmTokenRemoteDataSource {
  override suspend fun registerFcmToken(token: String) = withContext(appCoroutineDispatchers.io) {
    catchingApiException(remoteErrorMapper = remoteErrorMapper) {
      fcmTokenApiService.registerFcmToken(body = FcmTokenRequestBody(fcmToken = token))
    }
  }
}
