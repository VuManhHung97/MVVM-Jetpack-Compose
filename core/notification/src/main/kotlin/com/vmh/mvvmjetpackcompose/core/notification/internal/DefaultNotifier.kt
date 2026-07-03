package com.vmh.mvvmjetpackcompose.core.notification.internal

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.vmh.mvvmjetpackcompose.core.notification.Notifier
import com.vmh.mvvmjetpackcompose.core.notification.R
import com.vmh.mvvmjetpackcompose.core.notification.model.PushMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import timber.log.Timber

/**
 * The sole [Notifier] implementation, backed by [NotificationManagerCompat].
 *
 * Kept decoupled from the app module: the tap [android.app.PendingIntent] is built either from the
 * launch intent obtained via [android.content.pm.PackageManager] or from the message's deep link,
 * so this module never references `MainActivity`.
 */
internal class DefaultNotifier @Inject constructor(@param:ApplicationContext private val context: Context) : Notifier {

  // Class-owned monotonic id source so concurrent notifications do not overwrite each other.
  private val notificationIdCounter = AtomicInteger(0)

  override fun showNotification(message: PushMessage) {
    // Data-only messages carry no user-facing content; the app handles them silently.
    if (message.title == null && message.body == null) return

    val notificationManager = NotificationManagerCompat.from(context)
    if (!notificationManager.areNotificationsEnabled()) {
      Timber.w("Notifications are disabled; skipping message on channel ${message.channelType.id}")
      return
    }

    context.ensureNotificationChannel(message.channelType)

    val notification = NotificationCompat.Builder(context, message.channelType.id)
      .setSmallIcon(R.drawable.ic_stat_notification)
      .setContentTitle(message.title)
      .setContentText(message.body)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message.body))
      .setAutoCancel(true)
      .setContentIntent(context.buildContentIntent(message.deepLink))
      .build()

    // areNotificationsEnabled() above already covers the POST_NOTIFICATIONS runtime permission, but
    // guard against a SecurityException in case it is revoked between the check and this call.
    try {
      notificationManager.notify(notificationIdCounter.incrementAndGet(), notification)
    } catch (securityException: SecurityException) {
      Timber.w(securityException, "Missing POST_NOTIFICATIONS permission; notification not posted")
    }
  }
}

/**
 * Builds the tap intent: opens the deep link when present (kept in-app by scoping it to this
 * package), otherwise falls back to the app's launcher activity.
 */
private fun Context.buildContentIntent(deepLink: String?): PendingIntent? {
  val intent: Intent? = when {
    deepLink != null -> Intent(Intent.ACTION_VIEW, deepLink.toUri()).setPackage(packageName)
    else -> packageManager.getLaunchIntentForPackage(packageName)
  }
  return intent
    ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    ?.let { resolvedIntent ->
      PendingIntent.getActivity(
        this,
        0,
        resolvedIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
      )
    }
}
