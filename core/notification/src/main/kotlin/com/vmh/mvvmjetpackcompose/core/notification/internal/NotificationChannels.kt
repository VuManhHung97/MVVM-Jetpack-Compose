package com.vmh.mvvmjetpackcompose.core.notification.internal

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.vmh.mvvmjetpackcompose.core.notification.R
import com.vmh.mvvmjetpackcompose.core.notification.model.NotificationChannelType

/** Maps the framework-agnostic [NotificationChannelType.Importance] to a platform importance constant. */
private fun NotificationChannelType.Importance.toPlatformImportance(): Int = when (this) {
  NotificationChannelType.Importance.Low -> NotificationManager.IMPORTANCE_LOW
  NotificationChannelType.Importance.Default -> NotificationManager.IMPORTANCE_DEFAULT
  NotificationChannelType.Importance.High -> NotificationManager.IMPORTANCE_HIGH
}

/** The user-visible name of a channel. */
private fun Context.channelName(type: NotificationChannelType): String = when (type) {
  NotificationChannelType.Default -> getString(R.string.notification_channel_default_name)
}

/** The user-visible description of a channel, shown in the system settings. */
private fun Context.channelDescription(type: NotificationChannelType): String = when (type) {
  NotificationChannelType.Default -> getString(R.string.notification_channel_default_description)
}

/**
 * Registers the system channel for [type] if it does not exist yet. Creating a channel is
 * idempotent, so this is safe to call both at startup and right before posting a notification.
 *
 * Uses [NotificationManagerCompat] so it is a no-op below API 26 (where channels do not exist)
 * without any version guard at the call site.
 */
internal fun Context.ensureNotificationChannel(type: NotificationChannelType) {
  val channel = NotificationChannelCompat.Builder(type.id, type.importance.toPlatformImportance())
    .setName(channelName(type))
    .setDescription(channelDescription(type))
    .build()
  NotificationManagerCompat.from(this).createNotificationChannel(channel)
}
