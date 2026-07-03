package com.vmh.mvvmjetpackcompose.core.notification.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.google.firebase.messaging.FirebaseMessaging
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.notification.NotificationTokenProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * The sole implementation of [NotificationTokenProvider], wrapping the Firebase Messaging SDK.
 *
 * [tokenFlow] is served from [PushTokenBus] — refreshed tokens arrive via
 * [AppFirebaseMessagingService.onNewToken]; one-shot reads go straight to the SDK. Every SDK call
 * runs on [AppCoroutineDispatchers.io] because Firebase's `Task`s complete on background executors.
 */
internal class DefaultNotificationTokenProvider @Inject constructor(
  private val firebaseMessaging: FirebaseMessaging,
  private val tokenBus: PushTokenBus,
  private val dispatchers: AppCoroutineDispatchers,
) : NotificationTokenProvider {

  override val tokenFlow: Flow<String> = tokenBus.tokens

  override suspend fun currentToken(): Result<String, AppError> = withContext(dispatchers.io) {
    firebaseMessaging.token.awaitResult()
  }

  override suspend fun deleteToken(): Result<Unit, AppError> = withContext(dispatchers.io) {
    firebaseMessaging.deleteToken().awaitResult().map { }
  }

  override suspend fun subscribeToTopic(topic: String): Result<Unit, AppError> = withContext(dispatchers.io) {
    firebaseMessaging.subscribeToTopic(topic).awaitResult().map { }
  }

  override suspend fun unsubscribeFromTopic(topic: String): Result<Unit, AppError> = withContext(dispatchers.io) {
    firebaseMessaging.unsubscribeFromTopic(topic).awaitResult().map { }
  }
}
