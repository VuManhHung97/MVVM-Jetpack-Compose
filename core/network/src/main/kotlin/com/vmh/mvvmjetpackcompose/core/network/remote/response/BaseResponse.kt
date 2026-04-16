package com.vmh.mvvmjetpackcompose.core.network.remote.response

import androidx.annotation.Keep
import com.squareup.moshi.Json

sealed interface BaseResponse<out T> {
  val data: T

  @Keep
  data class Data<out T>(
    @param:Json(name = "data")
    override val data: T,
  ) : BaseResponse<T>

  @Keep
  data class DataWithTotalRowsAndCursor<out T>(
    @param:Json(name = "data")
    override val data: T,
    @param:Json(name = "cursor")
    val cursor: String?,
  ) : BaseResponse<T>
}
