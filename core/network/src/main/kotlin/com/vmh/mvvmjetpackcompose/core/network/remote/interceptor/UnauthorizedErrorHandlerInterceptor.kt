package com.vmh.mvvmjetpackcompose.core.network.remote.interceptor

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UnauthorizedErrorEventEmit
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

internal class UnauthorizedErrorHandlerInterceptor @Inject constructor(
  private val unauthorizedErrorEventEmit: UnauthorizedErrorEventEmit,
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val res = chain.proceed(chain.request())

    Timber.d("[UnauthorizedErrorHandlerInterceptor] code=${res.code}")
    if (res.code == HTTP_UNAUTHORIZED) {
      unauthorizedErrorEventEmit.emitEvent()
    }

    return res
  }
}
