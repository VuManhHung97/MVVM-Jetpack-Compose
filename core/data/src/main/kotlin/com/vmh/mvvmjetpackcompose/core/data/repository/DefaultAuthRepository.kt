package com.vmh.mvvmjetpackcompose.core.data.repository

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.CoroutineBindingScope
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.map
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.data.mapper.auth.toUser
import com.vmh.mvvmjetpackcompose.core.data.mapper.user.toLocalUser
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.local.datasource.AuthLocalDataSource
import com.vmh.mvvmjetpackcompose.core.model.auth.AuthenticationState
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.AuthRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UserRemoteDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class DefaultAuthRepository @Inject constructor(
  private val authRemoteDataSource: AuthRemoteDataSource,
  private val authLocalDataSource: AuthLocalDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
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
    coroutineBinding {
      val userResponse = authRemoteDataSource
        .signIn(
          email = email,
          password = password,
        )
        .bind()

      authLocalDataSource.update { userResponse.toLocalUser() }.bind()
    }
  }

  override fun observeAuthenticationState(): Flow<Result<AuthenticationState, AppError.LocalStorageException>> =
    authLocalDataSource.observeLocalUser()
      .map { localResult ->
        localResult.map {
          if (it != null) {
            AuthenticationState.Authenticated(user = it.toUser())
          } else {
            AuthenticationState.Unauthenticated
          }
        }
      }
      .distinctUntilChanged()

  override suspend fun refreshCurrentUser() = withContext(appCoroutineDispatchers.io) {
    coroutineBinding {
      authLocalDataSource.readLocalUser().bind() ?: return@coroutineBinding
      fetchUserProfileAndUpdateLocal()
    }
  }

  private suspend fun CoroutineBindingScope<AppError>.fetchUserProfileAndUpdateLocal() {
    val profileResponse = userRemoteDataSource.getProfile().bind()
    authLocalDataSource.update { profileResponse.toLocalUser() }.bind()
  }
}
