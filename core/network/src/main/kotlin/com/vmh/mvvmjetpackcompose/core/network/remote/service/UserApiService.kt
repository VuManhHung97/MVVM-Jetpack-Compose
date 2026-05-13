package com.vmh.mvvmjetpackcompose.core.network.remote.service

import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CHECK_ACCESS_TOKEN
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CUSTOM_HEADER
import com.vmh.mvvmjetpackcompose.core.network.remote.response.BaseResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.user.ProfileResponse
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers

internal interface UserApiService {

  @Headers("$CUSTOM_HEADER: $CHECK_ACCESS_TOKEN")
  @GET("v1/api/users/get_profile")
  suspend fun getProfile(): BaseResponse.Data<ProfileResponse>

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): UserApiService = retrofit.create()
  }
}
