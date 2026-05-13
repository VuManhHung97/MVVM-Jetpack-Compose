package com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl

import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.AuthRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.di.RemoteErrorMapper
import com.vmh.mvvmjetpackcompose.core.network.remote.di.catchingApiException
import com.vmh.mvvmjetpackcompose.core.network.remote.request.SignInRequestBody
import com.vmh.mvvmjetpackcompose.core.network.remote.request.SignUpRequestBody
import com.vmh.mvvmjetpackcompose.core.network.remote.service.AuthApiService
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class AuthRemoteDataSourceImpl @Inject constructor(
  private val authApiService: AuthApiService,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val remoteErrorMapper: RemoteErrorMapper,
) : AuthRemoteDataSource {
  override suspend fun signUp(email: String, password: String) = withContext(appCoroutineDispatchers.io) {
    catchingApiException(remoteErrorMapper = remoteErrorMapper) {
      authApiService.signUp(
        body = SignUpRequestBody(
          email = email,
          password = password,
        ),
      )
    }
  }

  override suspend fun signIn(email: String, password: String) = withContext(appCoroutineDispatchers.io) {
    catchingApiException(remoteErrorMapper = remoteErrorMapper) {
      authApiService.signIn(
        body = SignInRequestBody(
          email = email,
          password = password,
        ),
      ).data
    }
  }
}
