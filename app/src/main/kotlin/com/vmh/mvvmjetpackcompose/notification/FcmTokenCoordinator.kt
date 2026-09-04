package com.vmh.mvvmjetpackcompose.notification

import com.github.michaelbull.result.get
import com.github.michaelbull.result.onFailure
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineScope
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.FcmTokenManager
import com.vmh.mvvmjetpackcompose.core.model.auth.AuthenticationState
import com.vmh.mvvmjetpackcompose.core.notification.NotificationTokenProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Translates app-level lifecycle events into [FcmTokenManager] calls. Lives in the app module
 * because it is the only layer that can observe launch, authentication transitions, and token
 * refreshes together; the manager itself stays free of that wiring.
 *
 * Runs on the app-scoped [AppCoroutineScope] so it keeps working when the process is started in the
 * background by an FCM token refresh (no `Activity` present).
 */
@Singleton
internal class FcmTokenCoordinator @Inject constructor(
  private val appCoroutineScope: AppCoroutineScope,
  private val fcmTokenManager: FcmTokenManager,
  private val notificationTokenProvider: NotificationTokenProvider,
  private val authRepository: AuthRepository,
) {

  /** Call once from `Application.onCreate()`. */
  fun start() {
    appCoroutineScope.launch {
      fcmTokenManager.sendRegistrationToServerOnLaunch()
        .onFailure { Timber.e(it, "FCM registration on launch failed") }
    }

    appCoroutineScope.launch {
      notificationTokenProvider.tokenFlow.collect { token ->
        fcmTokenManager.sendRegistrationToServerOnNewToken(token)
          .onFailure { Timber.e(it, "FCM registration on new token failed") }
      }
    }

    appCoroutineScope.launch { observeLoginTransitions() }
  }

  /**
   * Registers the token on a fresh sign-in. Tracks the previous state so the initial emission
   * (already-authenticated on launch, handled by [FcmTokenManager.sendRegistrationToServerOnLaunch])
   * is not mistaken for a login.
   *
   * The sign-out case is intentionally not handled here: it is driven from the logout flow (see
   * [FcmTokenManager.clearFcmToken]) so the backend can be told to detach while the session is still
   * valid.
   */
  private suspend fun observeLoginTransitions() {
    var wasAuthenticated: Boolean? = null
    authRepository.observeAuthenticationState().collect { result ->
      // Ignore read errors: a transient failure must not be mistaken for an auth-state change.
      val authenticationState = result.get() ?: return@collect
      val isAuthenticated = authenticationState is AuthenticationState.Authenticated
      if (wasAuthenticated == false && isAuthenticated) {
        fcmTokenManager.sendRegistrationToServerOnSuccessfulLogin()
          .onFailure { Timber.e(it, "FCM registration on login failed") }
      }
      wasAuthenticated = isAuthenticated
    }
  }
}
