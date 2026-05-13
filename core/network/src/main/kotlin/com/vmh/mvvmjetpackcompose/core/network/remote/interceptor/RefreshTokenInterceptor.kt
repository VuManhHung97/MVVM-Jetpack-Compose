package com.vmh.mvvmjetpackcompose.core.network.remote.interceptor

import android.content.Context
import com.github.michaelbull.result.get
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.local.datasource.AuthLocalDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.let
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class RefreshTokenInterceptor @Inject internal constructor(
  private val authLocalDataSource: AuthLocalDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  @param:ApplicationContext private val context: Context,
) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    val localUser = runBlocking(appCoroutineDispatchers.io) {
      authLocalDataSource
        .readLocalUser()
        .get()
    }

    if (localUser?.refreshToken == null) {
      return unauthorizedResponse(request)
    }

    return chain.proceedWithHeader(
      request = request,
      token = localUser.refreshToken.value,
    )
  }

  private fun Interceptor.Chain.proceedWithHeader(request: Request, token: String): Response = request.newBuilder()
    .addCommonHeaders(
      token = token,
      userAgent = getUserAgent(),
      devicePlatform = getDevicePlatform(context = context),
      appVersion = context.getApplicationVersionNameOrNull(),
    )
    .build()
    .let(::proceed)
}
