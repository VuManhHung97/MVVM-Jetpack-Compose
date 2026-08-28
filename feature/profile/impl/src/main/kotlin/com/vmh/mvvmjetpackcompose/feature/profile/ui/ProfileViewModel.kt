package com.vmh.mvvmjetpackcompose.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.fold
import com.vmh.mvvmjetpackcompose.core.common.extension.mapNotNullToPersistentList
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.model.auth.AuthenticationState
import com.vmh.mvvmjetpackcompose.core.model.auth.AuthenticationState.Authenticated
import com.vmh.mvvmjetpackcompose.core.model.auth.User
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
  private val authRepository: AuthRepository,
  private val eventChannel: EventChannel<ProfileSingleEvent>,
) : ViewModel(eventChannel),
  HasEventFlow<ProfileSingleEvent> by eventChannel {
  private val _uiStateFlow =
    MutableStateFlow(ProfileUiState(profilesContent = ProfileUiState.ProfilesContentUiState.Loading))

  val uiStateFlow: StateFlow<ProfileUiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (ProfileUiState) -> ProfileUiState) = _uiStateFlow.update(f)

  init {
    syncProfile()

    observeAuthenticationState()
  }

  private fun observeAuthenticationState() {
    authRepository.observeAuthenticationState()
      .onEach { result ->
        result.fold(
          success = { authenticationState ->
            when (authenticationState) {
              is Authenticated ->
                emitState { state ->
                  val user = authenticationState.user
                  state.copy(
                    profilesContent = ProfileUiState.ProfilesContentUiState.Content(
                      items = user.toSettingItems(),
                    ),
                  )
                }

              is AuthenticationState.Unauthenticated ->
                emitState { state ->
                  val items = when (val currentContent = state.profilesContent) {
                    is ProfileUiState.ProfilesContentUiState.Content ->
                      currentContent.items.mapNotNullToPersistentList { item ->
                        when (item) {
                          is ProfileUiState.ProfileUiItem.Profile -> ProfileUiState.ProfileUiItem.Profile.Empty
                          is ProfileUiState.ProfileUiItem.SignOut -> null
                          else -> item
                        }
                      }

                    else ->
                      ProfileUiState.ProfileUiItem.InitialItems.mapNotNullToPersistentList { item ->
                        when (item) {
                          is ProfileUiState.ProfileUiItem.Profile -> ProfileUiState.ProfileUiItem.Profile.Empty
                          is ProfileUiState.ProfileUiItem.SignOut -> null
                          else -> item
                        }
                      }
                  }
                  state.copy(
                    profilesContent = ProfileUiState.ProfilesContentUiState.Content(items = items),
                  )
                }
            }
          },
          failure = { error ->
            Timber.e(error, "Failed to observeAuthenticationState")
            emitState { state ->
              state.copy(
                profilesContent = ProfileUiState.ProfilesContentUiState.Error(error),
              )
            }
          },
        )
      }
      .launchIn(viewModelScope)
  }

  private fun syncProfile() {
    viewModelScope.launch {
      val refreshResult = authRepository.refreshCurrentUser()
      refreshResult.fold(
        success = {
          Timber.d("syncProfile for SettingsScreen successfully")
        },
        failure = { error ->
          Timber.e(error, "Failed to syncProfile for SettingsScreen")
        },
      )
    }
  }

  internal fun logout() {
    viewModelScope.launch {
      authRepository.logout()
        .fold(
          success = {
            eventChannel.send(ProfileSingleEvent.LogoutSuccess)
          },
          failure = { error ->
            Timber.e(error, "Failed to logout")
            eventChannel.send(ProfileSingleEvent.LogoutFailure(error = error))
          },
        )
    }
  }
}

private fun User.toSettingItems(): PersistentList<ProfileUiState.ProfileUiItem> =
  ProfileUiState.ProfileUiItem.InitialItems.mapNotNullToPersistentList { item ->
    when (item) {
      is ProfileUiState.ProfileUiItem.Profile ->
        ProfileUiState.ProfileUiItem.Profile.Info(
          url = avatar,
          name = fullName,
          email = email,
        )

      else -> item
    }
  }.add(ProfileUiState.ProfileUiItem.SignOut)
