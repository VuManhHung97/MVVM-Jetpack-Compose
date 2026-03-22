package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.core.os.bundleOf
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.util.getParcelableCompat
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class SignUpUiState(
  val email: String,
  val password: String,
  val confirmPassword: String,
  val emailValidationStatus: ValidationStatus,
  val passwordValidationStatus: ValidationStatus,
  val confirmPasswordValidationStatus: ValidationStatus,
  val emailChangedByUser: Boolean,
  val passwordChangedByUser: Boolean,
  val confirmPasswordChangedByUser: Boolean,
  val isLoading: Boolean,
) : Parcelable {
  companion object {
    private const val VIEW_STATE_KEY =
      "com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.StateSaver"

    val Initial
      get() = SignUpUiState(
        email = "",
        password = "",
        confirmPassword = "",
        emailValidationStatus = ValidationStatus.Error.Email.Empty,
        passwordValidationStatus = ValidationStatus.Error.Password.Empty,
        confirmPasswordValidationStatus = ValidationStatus.Error.Password.Empty,
        emailChangedByUser = false,
        passwordChangedByUser = false,
        confirmPasswordChangedByUser = false,
        isLoading = false,
      )
  }

  internal class StateSaver {
    fun SignUpUiState.toBundle() = bundleOf(VIEW_STATE_KEY to this)

    inline fun restore(bundle: Bundle?, initial: () -> SignUpUiState) = bundle
      ?.getParcelableCompat(VIEW_STATE_KEY)
      ?: initial()
  }
}

sealed interface ValidationStatus : Parcelable {
  @Parcelize
  data object Valid : ValidationStatus

  sealed interface Error : ValidationStatus {
    sealed interface Email : Error {
      @Parcelize
      data object Empty : Email

      @Parcelize
      data object InvalidFormat : Email
    }

    sealed interface Password : Error {
      @Parcelize
      data object Empty : Password

      @Parcelize
      data object InvalidFormat : Password

      @Parcelize
      data object InvalidLength : Password

      @Parcelize
      data object NotMatchWithConfirmPassword : Password
    }
  }
}

@StringRes
fun ValidationStatus.Error.getErrorMessageResId(): Int = when (this) {
  ValidationStatus.Error.Email.Empty -> CoreResourceR.string.validation_empty_email
  ValidationStatus.Error.Email.InvalidFormat -> CoreResourceR.string.validation_invalid_email
  ValidationStatus.Error.Password.Empty -> CoreResourceR.string.validation_empty_password
  ValidationStatus.Error.Password.InvalidFormat ->
    CoreResourceR.string.validation_invalid_password_cannot_include_space

  ValidationStatus.Error.Password.InvalidLength ->
    CoreResourceR.string.validation_invalid_password_must_be_in_6_to_32_characters

  ValidationStatus.Error.Password.NotMatchWithConfirmPassword ->
    CoreResourceR.string.validation_password_does_not_match
}
