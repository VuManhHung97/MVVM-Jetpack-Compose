package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.fold
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.model.auth.AuthenticationState
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@HiltViewModel
class AuthenticationViewModel @Inject internal constructor(
  private val eventChannel: EventChannel<AuthenticationSingleEvent>,
  private val authRepository: AuthRepository,
) : ViewModel(eventChannel),
  HasEventFlow<AuthenticationSingleEvent> by eventChannel {
  init {
    authRepository.observeAuthenticationState()
      .onEach { result ->
        result.fold(
          success = { authenticationState ->
            when (authenticationState) {
              is AuthenticationState.Authenticated ->
                eventChannel.send(AuthenticationSingleEvent.NavigateToHome)

              is AuthenticationState.Unauthenticated -> Unit
            }
          },
          failure = { error ->
            Timber.e(error, "Failed to observe authentication state")
          },
        )
      }
      .launchIn(viewModelScope)
  }
}

@Immutable
sealed interface AuthenticationSingleEvent {
  data object NavigateToHome : AuthenticationSingleEvent
}
