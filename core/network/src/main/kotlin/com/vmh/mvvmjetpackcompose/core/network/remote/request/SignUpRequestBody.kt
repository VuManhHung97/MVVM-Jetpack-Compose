package com.vmh.mvvmjetpackcompose.core.network.remote.request

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class SignUpRequestBody(
  @param:Json(name = "email")
  val email: String,
  @param:Json(name = "password")
  val password: String,
)
