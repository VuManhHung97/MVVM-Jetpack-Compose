package com.vmh.mvvmjetpackcompose.core.network.remote.response

import androidx.annotation.Keep
import com.squareup.moshi.Json
import kotlin.collections.find
import kotlin.let

@Keep
data class ErrorResponse(@param:Json(name = "error") val error: ErrorDetailResponse? = null) {
  @Keep
  data class ErrorDetailResponse(
    @param:Json(name = "code") val code: String? = null,
    @param:Json(name = "message") val message: String? = null,
  )

  @Keep
  enum class ErrorCode(val code: String) {
    Unknown("900"),
    PhoneAlreadyExists("phone_already_exists"),
    InvalidPhoneNumber("invalid_phone_number"),
    ;

    companion object {
      fun from(code: String?): ErrorCode = code?.let { entries.find { it.code == code } } ?: Unknown
    }
  }
}
