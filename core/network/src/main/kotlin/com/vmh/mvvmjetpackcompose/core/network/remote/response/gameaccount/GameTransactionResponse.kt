package com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class GameTransactionResponse(
  @param:Json(name = "code") val code: String,
  @param:Json(name = "account_id") val accountId: String,
  @param:Json(name = "amount") val amount: Long,
  @param:Json(name = "method") val method: String,
  @param:Json(name = "time") val time: String,
  @param:Json(name = "type") val type: String,
)
