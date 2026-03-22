package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmh.mvvmjetpackcompose.core.common.util.isValidEmail
import com.vmh.mvvmjetpackcompose.core.common.util.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class SignUpViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
  private val stateSaver = SignUpUiState.StateSaver()
  private val _uiStateFlow = MutableStateFlow(
    stateSaver.restore(savedStateHandle[VIEW_STATE_BUNDLE_KEY]) {
      SignUpUiState.Initial
    },
  )

  val uiStateFlow: StateFlow<SignUpUiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (SignUpUiState) -> SignUpUiState) = _uiStateFlow.update(f)

  init {
    savedStateHandle.setSavedStateProvider(VIEW_STATE_BUNDLE_KEY) {
      stateSaver.run { uiStateFlow.value.toBundle() }
    }
  }

  fun onEmailChange(newValue: String) {
    emitState { state ->
      state.copy(
        email = newValue,
        emailChangedByUser = true,
        emailValidationStatus = when {
          newValue.isEmpty() -> ValidationStatus.Error.Email.Empty
          !newValue.isValidEmail() -> ValidationStatus.Error.Email.InvalidFormat
          else -> ValidationStatus.Valid
        },
      )
    }
  }

  fun onPasswordChange(newValue: String) {
    emitState { state ->
      state.copy(
        password = newValue,
        passwordChangedByUser = true,
        passwordValidationStatus =
        when {
          newValue.isEmpty() -> ValidationStatus.Error.Password.Empty

          newValue.length !in PASSWORD_LENGTH_RANGE -> ValidationStatus.Error.Password.InvalidLength

          !newValue.isValidPassword() -> ValidationStatus.Error.Password.InvalidFormat

          else -> ValidationStatus.Valid
        },
        confirmPasswordValidationStatus = if (newValue != state.confirmPassword) {
          ValidationStatus.Error.Password.NotMatchWithConfirmPassword
        } else {
          ValidationStatus.Valid
        },
      )
    }
  }

  fun onConfirmPasswordChange(newValue: String) {
    emitState { state ->
      state.copy(
        confirmPassword = newValue,
        confirmPasswordChangedByUser = true,
        confirmPasswordValidationStatus =
        when {
          newValue.isEmpty() -> ValidationStatus.Error.Password.Empty

          newValue != state.password -> ValidationStatus.Error.Password.NotMatchWithConfirmPassword

          else -> ValidationStatus.Valid
        },
      )
    }
  }

  fun signUp() {
    emitState { state ->
      state.copy(
        emailChangedByUser = true,
        passwordChangedByUser = true,
        confirmPasswordChangedByUser = true,
      )
    }

    val state = uiStateFlow.value
    val isValid = state.emailValidationStatus is ValidationStatus.Valid &&
      state.passwordValidationStatus is ValidationStatus.Valid &&
      state.confirmPasswordValidationStatus is ValidationStatus.Valid

    if (isValid) {
      signUpWithEmail()
    }
  }

  private fun signUpWithEmail() {
    emitState { it.copy(isLoading = true) }

    viewModelScope.launch {
      emitState { it.copy(isLoading = false) }
    }
  }

  companion object {
    private val PASSWORD_LENGTH_RANGE = 6..32
    private const val VIEW_STATE_BUNDLE_KEY =
      "com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.view_state"
  }
}
