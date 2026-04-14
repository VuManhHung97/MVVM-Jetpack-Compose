package com.vmh.mvvmjetpackcompose.core.network.remote.interceptor

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.let
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class NoAuthInterceptor @Inject internal constructor(@param:ApplicationContext private val context: Context) :
  Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    return chain.proceedWithHeader(request = request)
  }

  private fun Interceptor.Chain.proceedWithHeader(request: Request): Response = request.newBuilder()
    .addCommonHeaders(
      token = null,
      userAgent = getUserAgent(),
      devicePlatform = getDevicePlatform(context = context),
      appVersion = context.getApplicationVersionNameOrNull(),
    )
    .build()
    .let(::proceed)
}
