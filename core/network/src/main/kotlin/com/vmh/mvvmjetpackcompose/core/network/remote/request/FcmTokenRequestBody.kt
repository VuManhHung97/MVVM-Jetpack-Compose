package com.vmh.mvvmjetpackcompose.core.network.remote.request

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class FcmTokenRequestBody(
  @param:Json(name = "fcm_token")
  val fcmToken: String,
)
