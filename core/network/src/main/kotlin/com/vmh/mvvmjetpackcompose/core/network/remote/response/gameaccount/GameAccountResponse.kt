package com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class GameAccountResponse(
  @param:Json(name = "id") val id: String,
  @param:Json(name = "username") val username: String,
  @param:Json(name = "character") val character: String,
  @param:Json(name = "clan") val clan: String,
  @param:Json(name = "level") val level: Int,
  @param:Json(name = "balance") val balance: Long,
  @param:Json(name = "status") val status: String,
  @param:Json(name = "created") val created: String,
  @param:Json(name = "last_login") val lastLogin: String,
  @param:Json(name = "vip") val vip: String,
)
