package com.vmh.mvvmjetpackcompose.core.common.util

private val EMAIL_REGEX = Regex(
  "^[a-zA-Z0-9](?!.*[._%+-]{2})[a-zA-Z0-9._%+-]*[a-zA-Z0-9]@[a-zA-Z0-9](?!.*[.-]{2})[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$",
)

private val PASSWORD_REGEX = Regex("^[^\\s]*$")

fun String.isValidEmail(): Boolean = EMAIL_REGEX.matches(this)

fun String.isValidPassword(): Boolean = PASSWORD_REGEX.matches(this)
