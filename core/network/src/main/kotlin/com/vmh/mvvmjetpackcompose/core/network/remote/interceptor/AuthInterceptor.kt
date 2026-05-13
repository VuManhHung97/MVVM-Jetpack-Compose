package com.vmh.mvvmjetpackcompose.core.network.remote.interceptor

import android.content.Context
import com.github.michaelbull.result.get
import com.vmh.mvvmjetpackcompose.core.common.BuildConfig
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.local.LocalUser
import com.vmh.mvvmjetpackcompose.core.local.datasource.AuthLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.extention.toProtoStringValue
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CHECK_ACCESS_TOKEN
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CUSTOM_HEADER
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.NO_AUTH
import com.vmh.mvvmjetpackcompose.core.network.remote.service.RefreshTokenApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject
import javax.inject.Provider
import kotlin.also
import kotlin.let
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

internal class AuthInterceptor @Inject internal constructor(
  private val authLocalDataSource: AuthLocalDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val refreshTokenApiService: Provider<RefreshTokenApiService>,
  @param:ApplicationContext private val context: Context,
) : Interceptor {
  private val mutex = Mutex()

  @Suppress("ReturnCount")
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val customHeaderValue = request.headers.values(CUSTOM_HEADER)

    if (NO_AUTH in customHeaderValue) {
      return chain.proceedWithHeader(request, null)
    }

    val localUser = getCurrentLocalUser()
    if (CHECK_ACCESS_TOKEN in customHeaderValue && localUser?.accessToken == null) {
      return unauthorizedResponse(request)
    }

    val res = chain.proceedWithHeader(request, localUser?.accessToken?.value)
    if (res.code != HTTP_UNAUTHORIZED || localUser?.accessToken == null) {
      // Forward HTTP_UNAUTHORIZED error
      return res
    }

    val newAccessToken = runBlocking(appCoroutineDispatchers.io) {
      mutex.withLock { executeRefreshTokenIfNeeded(localUser = localUser) }
    }

    if (BuildConfig.DEBUG) {
      Timber.d("refreshedAccessToken=$newAccessToken")
    }

    return if (newAccessToken !== null) {
      res.close()
      chain.proceedWithHeader(
        request = request,
        token = newAccessToken,
      )
    } else {
      // Forward HTTP_UNAUTHORIZED error
      res
    }
  }

  private suspend fun executeRefreshTokenIfNeeded(localUser: LocalUser): String? {
    val currentToken = getCurrentLocalUser()
      ?.accessToken
      ?.value

    return when {
      currentToken == null -> {
        null
      }
      currentToken != localUser.accessToken.value -> currentToken
      else -> {
        val refreshTokenRes = refreshTokenApiService
          .get()
          .refreshToken()

        when (refreshTokenRes.code()) {
          HTTP_OK -> {
            refreshTokenRes.body()!!.data
              .accessToken
              .also { accessToken ->
                authLocalDataSource.update {
                  it
                    ?.toBuilder()
                    ?.setAccessToken(accessToken.toProtoStringValue())
                    ?.build()
                }
              }
          }

          HTTP_UNAUTHORIZED -> {
            // clear user local
            authLocalDataSource.update { null }
            null
          }

          else -> {
            // clear user local
            authLocalDataSource.update { null }
            null
          }
        }
      }
    }
  }

  private fun getCurrentLocalUser(): LocalUser? = runBlocking(appCoroutineDispatchers.io) {
    authLocalDataSource
      .readLocalUser()
      .get()
  }

  private fun Interceptor.Chain.proceedWithHeader(request: Request, token: String?): Response = request.newBuilder()
    .addCommonHeaders(
      token = token,
      userAgent = getUserAgent(),
      devicePlatform = getDevicePlatform(context = context),
      appVersion = context.getApplicationVersionNameOrNull(),
    )
    .build()
    .let(::proceed)
}
