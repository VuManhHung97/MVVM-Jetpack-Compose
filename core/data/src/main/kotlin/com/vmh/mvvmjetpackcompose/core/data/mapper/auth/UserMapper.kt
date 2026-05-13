package com.vmh.mvvmjetpackcompose.core.data.mapper.auth

import com.vmh.mvvmjetpackcompose.core.local.LocalUser
import com.vmh.mvvmjetpackcompose.core.local.mapper.toLocalDate
import com.vmh.mvvmjetpackcompose.core.model.auth.User

internal fun LocalUser.toUser(): User = User(
  id = id.value,
  email = email.value,
  fullName = fullName.value,
  avatar = if (hasAvatar()) {
    avatar.value
  } else {
    null
  },
  birthday = if (hasDateOfBirth()) {
    dateOfBirth.toLocalDate()
  } else {
    null
  },
  phoneNumber = phoneNumber.value,
)
