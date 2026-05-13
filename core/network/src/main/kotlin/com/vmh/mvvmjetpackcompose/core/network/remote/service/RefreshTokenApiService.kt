package com.vmh.mvvmjetpackcompose.core.network.remote.service

import com.vmh.mvvmjetpackcompose.core.network.remote.di.RefreshApiRetrofit
import com.vmh.mvvmjetpackcompose.core.network.remote.response.BaseResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.auth.RefreshTokenResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.POST

internal interface RefreshTokenApiService {
  @POST("v1/api/users/refresh_token")
  suspend fun refreshToken(): Response<BaseResponse.Data<RefreshTokenResponse>>

  companion object Factory {
    operator fun invoke(@RefreshApiRetrofit retrofit: Retrofit): RefreshTokenApiService = retrofit.create()
  }
}
