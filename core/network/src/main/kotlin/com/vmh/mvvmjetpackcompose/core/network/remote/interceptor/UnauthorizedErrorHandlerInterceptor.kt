package com.vmh.mvvmjetpackcompose.core.network.remote.interceptor

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UnauthorizedErrorEventEmit
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CUSTOM_HEADER
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.NO_AUTH
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

internal class UnauthorizedErrorHandlerInterceptor @Inject constructor(
  private val unauthorizedErrorEventEmit: UnauthorizedErrorEventEmit,
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val res = chain.proceed(request)

    Timber.d("[UnauthorizedErrorHandlerInterceptor] code=${res.code}")
    val isNoAuthRequest = NO_AUTH in request.headers.values(CUSTOM_HEADER)
    // A response without networkResponse was built locally by AuthInterceptor because there is no session
    val isFromServer = res.networkResponse != null
    if (res.code == HTTP_UNAUTHORIZED && !isNoAuthRequest && isFromServer) {
      unauthorizedErrorEventEmit.emitEvent()
    }

    return res
  }
}
