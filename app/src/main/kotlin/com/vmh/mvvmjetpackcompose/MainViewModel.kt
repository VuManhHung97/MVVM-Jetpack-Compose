package com.vmh.mvvmjetpackcompose

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.model.auth.AuthenticationState
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
internal class MainViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
  internal val startDestinationStateFlow: StateFlow<StartDestinationState> = flow {
    emitAll(startDestinationStateFlow())
  }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = StartDestinationState.Loading,
    )

  private fun startDestinationStateFlow(): Flow<StartDestinationState> = authRepository.observeAuthenticationState()
    .onStart {
      authRepository.refreshCurrentUser()
    }
    .map { result ->
      result.toStartDestinationState()
    }
}

private fun Result<AuthenticationState, AppError.LocalStorageException>.toStartDestinationState() = fold(
  success = { authenticationState ->
    when (authenticationState) {
      AuthenticationState.Unauthenticated ->
        StartDestinationState.AuthenticationScreen

      is AuthenticationState.Authenticated ->
        StartDestinationState.MainScreen
    }
  },
  failure = {
    StartDestinationState.AuthenticationScreen
  },
)

@Immutable
sealed interface StartDestinationState {
  data object Loading : StartDestinationState
  data object MainScreen : StartDestinationState

  data object AuthenticationScreen : StartDestinationState
}
