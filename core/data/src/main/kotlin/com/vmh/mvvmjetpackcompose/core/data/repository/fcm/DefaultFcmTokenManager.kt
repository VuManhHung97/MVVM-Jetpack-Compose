package com.vmh.mvvmjetpackcompose.core.data.repository.fcm

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.map
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.domain.repository.FcmTokenManager
import com.vmh.mvvmjetpackcompose.core.local.LocalFcmToken
import com.vmh.mvvmjetpackcompose.core.local.datasource.AuthLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.datasource.FcmTokenLocalDataSource
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.FcmTokenRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.notification.NotificationTokenProvider
import javax.inject.Inject
import kotlinx.coroutines.withContext

/**
 * Orchestrates the FCM token data sources: reads the token from [NotificationTokenProvider],
 * caches it via [FcmTokenLocalDataSource], and pushes it to the backend through
 * [FcmTokenRemoteDataSource] — but only while a user is authenticated ([AuthLocalDataSource]).
 *
 * This class contains only the sync logic; serialization across the concurrent launch/login/refresh
 * triggers is layered on top by [SynchronizedFcmTokenManager], so each method here can assume it
 * runs without overlapping itself.
 */
internal class DefaultFcmTokenManager @Inject constructor(
  private val notificationTokenProvider: NotificationTokenProvider,
  private val fcmTokenLocalDataSource: FcmTokenLocalDataSource,
  private val fcmTokenRemoteDataSource: FcmTokenRemoteDataSource,
  private val authLocalDataSource: AuthLocalDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : FcmTokenManager {

  override suspend fun sendRegistrationToServerOnLaunch(): Result<Unit, AppError> =
    withContext(appCoroutineDispatchers.io) {
      coroutineBinding {
        if (!isAuthenticated().bind()) return@coroutineBinding

        val token = notificationTokenProvider.currentToken().bind()
        val localFcmToken = fcmTokenLocalDataSource.readFcmToken().bind()

        // Skip the network call when the current token is already registered for this session.
        if (localFcmToken != null && localFcmToken.isSynced && localFcmToken.token == token) {
          return@coroutineBinding
        }

        registerToken(token).bind()
      }
    }

  override suspend fun sendRegistrationToServerOnSuccessfulLogin(): Result<Unit, AppError> =
    withContext(appCoroutineDispatchers.io) {
      coroutineBinding {
        // A fresh login always (re)registers so the token is attributed to the new user.
        val token = notificationTokenProvider.currentToken().bind()
        registerToken(token).bind()
      }
    }

  override suspend fun sendRegistrationToServerOnNewToken(token: String): Result<Unit, AppError> =
    withContext(appCoroutineDispatchers.io) {
      coroutineBinding {
        // Persist the new token immediately, even when signed out, so a later launch/login can sync it.
        fcmTokenLocalDataSource.update { localFcmToken(token = token, isSynced = false) }.bind()

        if (!isAuthenticated().bind()) return@coroutineBinding

        registerToken(token).bind()
      }
    }

  /**
   * Detaches the token on logout: clears the local cache first, then registers an empty token so the
   * backend stops targeting this device for the user who is signing out.
   *
   * Must be invoked while the session is still valid (the register endpoint requires an access
   * token) — see [com.vmh.mvvmjetpackcompose.core.data.repository.DefaultAuthRepository.logout].
   */
  override suspend fun clearFcmToken(): Result<Unit, AppError> = withContext(appCoroutineDispatchers.io) {
    coroutineBinding {
      fcmTokenLocalDataSource.update { null }.bind()
      fcmTokenRemoteDataSource.registerFcmToken("").bind()
    }
  }

  /** Sends [token] to the backend and, on success, marks it synced locally. */
  private suspend fun registerToken(token: String): Result<Unit, AppError> = coroutineBinding {
    fcmTokenRemoteDataSource.registerFcmToken(token).bind()
    fcmTokenLocalDataSource.update { localFcmToken(token = token, isSynced = true) }.bind()
  }

  private suspend fun isAuthenticated(): Result<Boolean, AppError> =
    authLocalDataSource.readLocalUser().map { localUser -> localUser != null }

  private fun localFcmToken(token: String, isSynced: Boolean): LocalFcmToken = LocalFcmToken.newBuilder()
    .setToken(token)
    .setIsSynced(isSynced)
    .build()
}
