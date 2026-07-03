package com.vmh.mvvmjetpackcompose.core.notification.internal

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A UI-independent, app-scoped bus that bridges the framework-instantiated
 * [AppFirebaseMessagingService] to the injectable [NotificationTokenProvider]
 * [com.vmh.mvvmjetpackcompose.core.notification.NotificationTokenProvider].
 *
 * `onNewToken` fires on a Firebase-owned thread outside any Hilt scope, so the service pushes the
 * token here and collectors observe it through the provider. `replay = 1` lets a collector that
 * subscribes after a refresh still receive the latest token.
 */
@Singleton
internal class PushTokenBus @Inject constructor() {
  private val _tokens = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 1)
  val tokens: SharedFlow<String> = _tokens.asSharedFlow()

  fun publish(token: String) {
    _tokens.tryEmit(token)
  }
}
