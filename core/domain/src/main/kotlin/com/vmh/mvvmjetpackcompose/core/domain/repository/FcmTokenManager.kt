package com.vmh.mvvmjetpackcompose.core.domain.repository

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError

/**
 * Coordinates syncing the device's FCM registration token with the backend.
 *
 * The token is only ever sent while a user is authenticated (the backend endpoint attributes it to
 * the current user). Every entry point is serialized internally so overlapping triggers — launch,
 * login, and a token refresh arriving at once — cannot race each other.
 *
 * All functions are best-effort and return a [Result] purely so the caller can log failures; a
 * failed sync never affects the calling flow.
 */
interface FcmTokenManager {

  /**
   * Called once per app start. Sends the current token to the backend if the user is authenticated
   * and it has not already been synced for this session.
   */
  suspend fun sendRegistrationToServerOnLaunch(): Result<Unit, AppError>

  /**
   * Called right after a successful sign-in. Always (re)sends the current token so it is associated
   * with the newly authenticated user.
   */
  suspend fun sendRegistrationToServerOnSuccessfulLogin(): Result<Unit, AppError>

  /**
   * Called when Firebase issues a new token. Persists it locally and, if the user is authenticated,
   * sends it to the backend.
   */
  suspend fun sendRegistrationToServerOnNewToken(token: String): Result<Unit, AppError>

  /**
   * Called on logout. Clears the locally cached token and tells the backend to detach it by
   * registering an empty token, so the device stops receiving the signed-out user's messages.
   *
   * Must run while the session is still valid — the register endpoint requires an access token — so
   * it is invoked at the start of the logout flow, before the credentials are cleared.
   */
  suspend fun clearFcmToken(): Result<Unit, AppError>
}
