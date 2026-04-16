package com.vmh.mvvmjetpackcompose.core.model.auth

import java.time.LocalDate

data class User(
  val id: Long,
  val email: String,
  val fullName: String,
  val avatar: String?,
  val birthday: LocalDate?,
  val phoneNumber: String,
)
