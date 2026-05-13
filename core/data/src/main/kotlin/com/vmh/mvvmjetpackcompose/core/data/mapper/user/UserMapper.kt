package com.vmh.mvvmjetpackcompose.core.data.mapper.user

import com.google.protobuf.StringValue
import com.google.protobuf.UInt64Value
import com.vmh.mvvmjetpackcompose.core.local.LocalUser
import com.vmh.mvvmjetpackcompose.core.local.extention.toProtoStringValue
import com.vmh.mvvmjetpackcompose.core.local.mapper.toTimestamp
import com.vmh.mvvmjetpackcompose.core.network.remote.response.user.ProfileResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.user.UserResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
import java.time.format.ResolverStyle
import java.util.Locale
import kotlin.fold
import kotlin.runCatching
import kotlin.text.isNullOrEmpty

internal val DATE_OF_BIRTH_FORMATTER: DateTimeFormatter by lazy {
  DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ROOT) // use uuuu (proleptic year)
    .withResolverStyle(ResolverStyle.STRICT) // reject invalid dates
    .withDecimalStyle(DecimalStyle.STANDARD) // enforce ASCII digits 0-9
}

internal fun UserResponse.toLocalUser(): LocalUser = LocalUser.newBuilder()
  .setId(UInt64Value.of(id))
  .setEmail(StringValue.of(email))
  .setFullName(fullName.toProtoStringValue())
  .setOrClearAvatar(avatar = avatar)
  .setOrClearDateOfBirth(dateOfBirth = dateOfBirth)
  .setPhoneNumber(phoneNumber.toProtoStringValue())
  .setAccessToken(accessToken.toProtoStringValue())
  .setRefreshToken(refreshToken.toProtoStringValue())
  .build()

internal fun LocalUser.updateProfile(profile: ProfileResponse): LocalUser = toBuilder()
  .setId(UInt64Value.of(profile.id))
  .setEmail(StringValue.of(profile.email))
  .setFullName(profile.fullName.toProtoStringValue())
  .setOrClearAvatar(avatar = profile.avatar)
  .setOrClearDateOfBirth(dateOfBirth = profile.dateOfBirth)
  .setPhoneNumber(profile.phoneNumber.toProtoStringValue())
  .build()

@Suppress("NOTHING_TO_INLINE")
private inline fun LocalUser.Builder.setOrClearAvatar(avatar: String?): LocalUser.Builder =
  if (avatar.isNullOrEmpty()) {
    clearAvatar()
  } else {
    setAvatar(StringValue.of(avatar))
  }

@Suppress("NOTHING_TO_INLINE")
private inline fun LocalUser.Builder.setOrClearDateOfBirth(dateOfBirth: String?): LocalUser.Builder =
  if (dateOfBirth.isNullOrEmpty()) {
    clearDateOfBirth()
  } else {
    runCatching { LocalDate.parse(dateOfBirth, DATE_OF_BIRTH_FORMATTER) }
      .fold(
        onSuccess = { setDateOfBirth(it.toTimestamp()) },
        onFailure = { clearDateOfBirth() },
      )
  }
