package com.vmh.mvvmjetpackcompose.core.network.remote.service

import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CUSTOM_HEADER
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.NO_AUTH
import com.vmh.mvvmjetpackcompose.core.network.remote.request.SignInRequestBody
import com.vmh.mvvmjetpackcompose.core.network.remote.request.SignUpRequestBody
import com.vmh.mvvmjetpackcompose.core.network.remote.response.BaseResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.user.UserResponse
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface AuthApiService {
  @Headers("$CUSTOM_HEADER: $NO_AUTH")
  @POST("v1/api/users/signup")
  suspend fun signUp(@Body body: SignUpRequestBody)

  @Headers("$CUSTOM_HEADER: $NO_AUTH")
  @POST("v1/api/users/login")
  suspend fun signIn(@Body body: SignInRequestBody): BaseResponse.Data<UserResponse>

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): AuthApiService = retrofit.create()
  }
}
