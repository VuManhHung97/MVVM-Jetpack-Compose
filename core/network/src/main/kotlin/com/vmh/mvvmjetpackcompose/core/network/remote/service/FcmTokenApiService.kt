package com.vmh.mvvmjetpackcompose.core.network.remote.service

import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CHECK_ACCESS_TOKEN
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CUSTOM_HEADER
import com.vmh.mvvmjetpackcompose.core.network.remote.request.FcmTokenRequestBody
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT

internal interface FcmTokenApiService {

  // Requires a valid access token — the endpoint associates the FCM token with the current user.
  @Headers("$CUSTOM_HEADER: $CHECK_ACCESS_TOKEN")
  @PUT("customers/fcm-token")
  suspend fun registerFcmToken(@Body body: FcmTokenRequestBody)

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): FcmTokenApiService = retrofit.create()
  }
}
