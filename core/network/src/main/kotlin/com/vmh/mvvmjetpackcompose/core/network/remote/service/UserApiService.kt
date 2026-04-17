package com.vmh.mvvmjetpackcompose.core.network.remote.service

import com.vmh.mvvmjetpackcompose.core.network.remote.response.BaseResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.user.ProfileResponse
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET

internal interface UserApiService {
  @GET("v1/api/users/get_profile")
  suspend fun getProfile(): BaseResponse.Data<ProfileResponse>

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): UserApiService = retrofit.create()
  }
}
