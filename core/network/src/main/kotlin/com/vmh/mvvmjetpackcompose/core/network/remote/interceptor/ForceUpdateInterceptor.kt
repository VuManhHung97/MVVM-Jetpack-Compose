package com.vmh.mvvmjetpackcompose.core.network.remote.interceptor

import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.ForceUpdateErrorEventEmit
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

internal class ForceUpdateInterceptor @Inject internal constructor(
  private val forceUpdateErrorEventEmit: ForceUpdateErrorEventEmit,
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val response = chain.proceed(chain.request())
    if (response.code == AppError.ApiException.ServerException.StatusCode.UpgradeRequired.code) {
      forceUpdateErrorEventEmit.emitEvent()
    }
    return response
  }
}
