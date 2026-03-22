package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class SignInViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
  private val stateSaver = SignInUiState.StateSaver()

  private val _uiStateFlow = MutableStateFlow(
    stateSaver.restore(savedStateHandle[VIEW_STATE_BUNDLE_KEY]) { SignInUiState.initial },
  )

  val uiStateFlow: StateFlow<SignInUiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (SignInUiState) -> SignInUiState) = _uiStateFlow.update(f)

  init {
    savedStateHandle.setSavedStateProvider(VIEW_STATE_BUNDLE_KEY) {
      stateSaver.run { uiStateFlow.value.toBundle() }
    }
  }

  fun emailChanged(newValue: String) {
    emitState { state ->
      state.copy(
        email = newValue,
        emailChangedByUser = true,
        emailValidationStatus = when {
          newValue.isEmpty() -> ValidationStatus.Error.Email.Empty

          else -> ValidationStatus.Valid
        },
      )
    }
  }

  fun passwordChanged(newValue: String) {
    emitState { state ->
      state.copy(
        password = newValue,
        passwordChangedByUser = true,
        passwordValidationStatus = when {
          newValue.isEmpty() -> ValidationStatus.Error.Password.Empty

          else -> ValidationStatus.Valid
        },
      )
    }
  }

  fun signIn() {
    emitState { state ->
      state.copy(
        emailChangedByUser = true,
        passwordChangedByUser = true,
      )
    }

    val state = uiStateFlow.value
    val isValid = state.emailValidationStatus is ValidationStatus.Valid &&
      state.passwordValidationStatus is ValidationStatus.Valid

    if (isValid) {
      signInWithEmail()
    }
  }

  private fun signInWithEmail() {
    emitState { it.copy(isLoading = true) }

    viewModelScope.launch {
      // TODO:
      emitState { it.copy(isLoading = false) }
    }
  }

  companion object {
    private const val VIEW_STATE_BUNDLE_KEY =
      "com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.view_state"
  }
}
