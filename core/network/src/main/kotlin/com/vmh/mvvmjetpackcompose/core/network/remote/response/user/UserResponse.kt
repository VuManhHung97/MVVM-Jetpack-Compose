package com.vmh.mvvmjetpackcompose.core.network.remote.response.user

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class UserResponse(
  @param:Json(name = "id") val id: Long,
  @param:Json(name = "email") val email: String,
  @param:Json(name = "token") val accessToken: String,
  @param:Json(name = "refreshToken") val refreshToken: String,
  @param:Json(name = "fullname") val fullName: String?,
  @param:Json(name = "avatarUrl") val avatar: String?,
  @param:Json(name = "date_of_birth") val dateOfBirth: String?,
  @param:Json(name = "phone") val phoneNumber: String?,
)
