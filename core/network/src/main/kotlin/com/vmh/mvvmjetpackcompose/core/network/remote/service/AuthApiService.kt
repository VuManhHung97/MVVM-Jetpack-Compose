package com.vmh.mvvmjetpackcompose.core.network.remote.service

import com.vmh.mvvmjetpackcompose.core.network.remote.request.SignInRequestBody
import com.vmh.mvvmjetpackcompose.core.network.remote.request.SignUpRequestBody
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthApiService {
  @POST("v1/api/users/signup")
  suspend fun signUp(@Body body: SignUpRequestBody)

  @POST("v1/api/users/login")
  suspend fun signIn(@Body body: SignInRequestBody)

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): AuthApiService = retrofit.create()
  }
}
