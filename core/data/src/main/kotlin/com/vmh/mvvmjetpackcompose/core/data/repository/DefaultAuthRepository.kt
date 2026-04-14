package com.vmh.mvvmjetpackcompose.core.data.repository

import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.AuthRemoteDataSource
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class DefaultAuthRepository @Inject constructor(
  private val authRemoteDataSource: AuthRemoteDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : AuthRepository {
  override suspend fun signUp(email: String, password: String) = withContext(appCoroutineDispatchers.io) {
    authRemoteDataSource
      .signUp(
        email = email,
        password = password,
      )
  }

  override suspend fun signIn(email: String, password: String) = withContext(appCoroutineDispatchers.io) {
    authRemoteDataSource
      .signIn(
        email = email,
        password = password,
      )
  }
}
