package com.vmh.mvvmjetpackcompose.core.network.remote.request

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class DepositRequestBody(
  @param:Json(name = "amount") val amount: Long,
  @param:Json(name = "note") val note: String?,
)

@Keep
data class LockAccountRequestBody(
  @param:Json(name = "is_locked") val isLocked: Boolean,
  @param:Json(name = "reason") val reason: String?,
)
