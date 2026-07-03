package com.vmh.mvvmjetpackcompose.core.notification.internal

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vmh.mvvmjetpackcompose.core.notification.Notifier
import com.vmh.mvvmjetpackcompose.core.notification.model.NotificationChannelType
import com.vmh.mvvmjetpackcompose.core.notification.model.PushMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import timber.log.Timber

/**
 * Receives FCM callbacks. Instantiated by the Android framework, so Hilt injects its dependencies
 * via [@AndroidEntryPoint] rather than constructor injection.
 *
 * This is the only class in the app that touches Firebase's [RemoteMessage]; it immediately maps
 * the payload to the module's own [PushMessage] and forwards a refreshed token through
 * [PushTokenBus], keeping the Firebase SDK contained within this module.
 */
@AndroidEntryPoint
internal class AppFirebaseMessagingService : FirebaseMessagingService() {

  @Inject
  lateinit var notifier: Notifier

  @Inject
  lateinit var tokenBus: PushTokenBus

  override fun onNewToken(token: String) {
    Timber.tag(TAG).d("Refreshed token: $token")
    tokenBus.publish(token)
  }

  override fun onMessageReceived(message: RemoteMessage) {
    Timber.d("From: ${message.from}")

    val notification = message.notification
    Timber.tag(TAG).d("Message data payload: ${message.data}")
    Timber.tag(TAG).d("Notification data: $notification")

    // Foreground messages are always delivered here; background notification-messages are handled
    // by the system unless they also carry a data payload.
    notifier.showNotification(message.toPushMessage())
  }

  companion object {
    private const val TAG = "AppFirebaseMessagingService"
  }
}

/** Maps a Firebase [RemoteMessage] to the module's transport-agnostic [PushMessage]. */
private fun RemoteMessage.toPushMessage(): PushMessage = PushMessage(
  title = notification?.title ?: data[KEY_TITLE],
  body = notification?.body ?: data[KEY_BODY],
  channelType = NotificationChannelType.Default,
  deepLink = data[KEY_DEEP_LINK],
  data = data,
)

private const val KEY_TITLE = "title"
private const val KEY_BODY = "body"
private const val KEY_DEEP_LINK = "deep_link"
