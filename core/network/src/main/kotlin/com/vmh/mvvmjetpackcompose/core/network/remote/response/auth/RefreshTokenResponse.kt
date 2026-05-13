package com.vmh.mvvmjetpackcompose.core.network.remote.response.auth

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class RefreshTokenResponse(@param:Json(name = "token") val accessToken: String)
