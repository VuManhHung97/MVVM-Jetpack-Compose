package com.vmh.mvvmjetpackcompose.core.notification

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import kotlinx.coroutines.flow.Flow

/**
 * Provides access to the device's FCM registration token and topic subscriptions.
 *
 * This is the only abstraction over Firebase's token APIs; the implementation is `internal`.
 * Inject it wherever the token needs to be synced to the backend (typically by collecting
 * [tokenFlow] and calling an API repository).
 */
interface NotificationTokenProvider {
  /**
   * Emits the FCM token every time Firebase issues a new one (install, data reset, periodic
   * rotation). Backed by an app-scoped hot flow, so late collectors receive the most recent token.
   */
  val tokenFlow: Flow<String>

  /** Fetches the current FCM registration token, requesting a fresh one if none exists yet. */
  suspend fun currentToken(): Result<String, AppError>

  /** Deletes the current token — call on sign-out so the device stops receiving the user's pushes. */
  suspend fun deleteToken(): Result<Unit, AppError>

  /** Subscribes the device to a topic so it receives messages sent to that topic. */
  suspend fun subscribeToTopic(topic: String): Result<Unit, AppError>

  /** Unsubscribes the device from a previously subscribed topic. */
  suspend fun unsubscribeFromTopic(topic: String): Result<Unit, AppError>
}
