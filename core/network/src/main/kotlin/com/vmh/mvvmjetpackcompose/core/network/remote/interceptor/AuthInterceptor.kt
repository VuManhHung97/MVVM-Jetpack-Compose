package com.vmh.mvvmjetpackcompose.core.network.remote.interceptor

import android.content.Context
import com.vmh.mvvmjetpackcompose.core.common.BuildConfig
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UnauthorizedErrorEventEmit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject
import kotlin.also
import kotlin.let
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

internal class AuthInterceptor @Inject internal constructor(
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val unauthorizedErrorEventEmit: UnauthorizedErrorEventEmit,
  @param:ApplicationContext private val context: Context,
) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    val accessToken: String? = runBlocking(appCoroutineDispatchers.io) {
      null
    }

    val userAgent: String? = getUserAgent()
    val devicePlatform: String? = getDevicePlatform(context = context)

    if (BuildConfig.DEBUG) {
      Timber.d("accessToken=$accessToken")
      Timber.d("userAgent=$userAgent")
      Timber.d("devicePlatform=$devicePlatform")
    }

    val response = chain.proceedWithHeader(
      request = request,
      token = accessToken,
      userAgent = userAgent,
      devicePlatform = devicePlatform,
    )
    if (response.code != HTTP_UNAUTHORIZED) {
      return response
    }

    val refreshedAccessToken = runBlocking(appCoroutineDispatchers.io) {
      ""
    }

    if (BuildConfig.DEBUG) {
      Timber.d("refreshedAccessToken=$refreshedAccessToken")
    }

    return if (refreshedAccessToken != null) {
      response.close()
      chain.proceedWithHeader(
        request = request,
        token = refreshedAccessToken,
        userAgent = userAgent,
        devicePlatform = devicePlatform,
      ).also {
        if (it.code == HTTP_UNAUTHORIZED) {
          unauthorizedErrorEventEmit.emitEvent()
        }
      }
    } else {
      unauthorizedErrorEventEmit.emitEvent()
      response
    }
  }

  private fun Interceptor.Chain.proceedWithHeader(
    request: Request,
    token: String?,
    userAgent: String?,
    devicePlatform: String?,
  ): Response = request.newBuilder()
    .addCommonHeaders(
      token = token,
      userAgent = userAgent,
      devicePlatform = devicePlatform,
      appVersion = context.getApplicationVersionNameOrNull(),
    )
    .build()
    .let(::proceed)
}
