package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.core.os.bundleOf
import com.vmh.mvvmjetpackcompose.core.ui.util.getParcelableCompat
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class SignInUiState(
  val email: String,
  val password: String,
  val emailValidationStatus: ValidationStatus,
  val passwordValidationStatus: ValidationStatus,
  val emailChangedByUser: Boolean,
  val passwordChangedByUser: Boolean,
  val isLoading: Boolean,
) : Parcelable {
  companion object {
    private const val VIEW_STATE_KEY =
      "com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.StateSaver"

    val initial
      get() = SignInUiState(
        email = "",
        password = "",
        emailValidationStatus = ValidationStatus.Error.Email.Empty,
        passwordValidationStatus = ValidationStatus.Error.Password.Empty,
        emailChangedByUser = false,
        passwordChangedByUser = false,
        isLoading = false,
      )
  }

  internal class StateSaver {
    fun SignInUiState.toBundle() = bundleOf(VIEW_STATE_KEY to this)

    inline fun restore(bundle: Bundle?, initial: () -> SignInUiState) = bundle
      ?.getParcelableCompat(VIEW_STATE_KEY)
      ?: initial()
  }
}

sealed interface ValidationStatus : Parcelable {
  @Parcelize
  data object Valid : ValidationStatus

  @Parcelize
  sealed interface Error : ValidationStatus {
    sealed interface Email : Error {
      @Parcelize
      data object Empty : Email
    }

    sealed interface Password : Error {
      @Parcelize
      data object Empty : Password
    }
  }
}
