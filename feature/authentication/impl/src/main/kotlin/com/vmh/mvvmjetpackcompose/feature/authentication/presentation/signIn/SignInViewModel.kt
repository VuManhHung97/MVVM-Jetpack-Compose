package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.fold
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsTracker
import com.vmh.mvvmjetpackcompose.core.analytics.logLogin
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
internal class SignInViewModel @Inject constructor(
  private val authRepository: AuthRepository,
  private val analyticsTracker: AnalyticsTracker,
  private val eventChannel: EventChannel<SignInSingleEvent>,
  savedStateHandle: SavedStateHandle,
) : ViewModel(eventChannel),
  HasEventFlow<SignInSingleEvent> by eventChannel {
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
      val state = uiStateFlow.value
      authRepository
        .signIn(
          email = state.email,
          password = state.password,
        )
        .fold(
          success = {
            analyticsTracker.logLogin(method = LOGIN_METHOD_EMAIL)
            emitState { it.copy(isLoading = false) }
            eventChannel.send(SignInSingleEvent.SignInSuccess)
          },
          failure = { error ->
            Timber.e(error, "signInWithEmail failure")
            emitState { it.copy(isLoading = false) }
            eventChannel.send(SignInSingleEvent.SignInFailure(error = error))
          },
        )
    }
  }

  companion object {
    private const val VIEW_STATE_BUNDLE_KEY =
      "com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.view_state"

    private const val LOGIN_METHOD_EMAIL = "email"
  }
}
