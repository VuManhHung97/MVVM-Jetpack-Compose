package com.vmh.mvvmjetpackcompose.core.network.remote.request

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class SignInRequestBody(
  @param:Json(name = "email")
  val email: String,
  @param:Json(name = "password")
  val password: String,
)
