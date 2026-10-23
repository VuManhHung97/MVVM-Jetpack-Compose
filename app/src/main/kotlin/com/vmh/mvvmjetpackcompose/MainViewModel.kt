package com.vmh.mvvmjetpackcompose

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.onFailure
import com.vmh.mvvmjetpackcompose.core.deeplink.DeepLinkDestination
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.ForceUpdateErrorEventRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.UnauthorizedErrorEventRepository
import com.vmh.mvvmjetpackcompose.core.model.auth.AuthenticationState
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
internal class MainViewModel @Inject constructor(
  private val authRepository: AuthRepository,
  private val eventChannel: EventChannel<MainSingleEvent>,
  unauthorizedErrorEventRepository: UnauthorizedErrorEventRepository,
  forceUpdateErrorEventRepository: ForceUpdateErrorEventRepository,
) : ViewModel(eventChannel),
  HasEventFlow<MainSingleEvent> by eventChannel {

  internal val unauthorizedErrorEventFlow = unauthorizedErrorEventRepository
    .events
    .buffer(capacity = Channel.UNLIMITED)
    .produceIn(viewModelScope)
    .receiveAsFlow()

  internal val forceUpdateErrorEventFlow = forceUpdateErrorEventRepository.events
    .buffer(Channel.UNLIMITED)
    .produceIn(viewModelScope)
    .receiveAsFlow()

  internal val startDestinationStateFlow: StateFlow<StartDestinationState> = flow {
    emitAll(startDestinationStateFlow())
  }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = StartDestinationState.Loading,
    )

  init {
    viewModelScope.launch {
      authRepository.refreshCurrentUser()
        .onFailure { error -> Timber.e(error, "MainViewModel refreshCurrentUser failure") }
    }
  }

  private fun startDestinationStateFlow(): Flow<StartDestinationState> = authRepository.observeAuthenticationState()
    .map { result ->
      result.toStartDestinationState()
    }

  internal fun handleDeepLinkDestination(deepLinkDestination: DeepLinkDestination) {
    when (deepLinkDestination) {
      is DeepLinkDestination.Search ->
        viewModelScope.launch {
          val startDestination = startDestinationStateFlow.first { it != StartDestinationState.Loading }
          if (startDestination == StartDestinationState.MainScreen) {
            eventChannel.send(MainSingleEvent.NavigateToSearch(keyword = deepLinkDestination.keyword))
          }
        }
    }
  }

  internal fun logout() {
    viewModelScope.launch {
      authRepository.logout()
        .fold(
          success = { eventChannel.send(MainSingleEvent.NavigateToAuthentication) },
          failure = { error -> Timber.e(error, "MainViewModel logout failure") },
        )
    }
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
