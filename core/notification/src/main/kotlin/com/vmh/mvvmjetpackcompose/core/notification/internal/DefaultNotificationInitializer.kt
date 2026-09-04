package com.vmh.mvvmjetpackcompose.core.notification.internal

import android.content.Context
import com.vmh.mvvmjetpackcompose.core.notification.NotificationInitializer
import com.vmh.mvvmjetpackcompose.core.notification.model.NotificationChannelType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Creates every [NotificationChannelType] channel up front so they exist before the first
 * notification — including ones the OS posts from an FCM payload while the app is backgrounded.
 */
internal class DefaultNotificationInitializer @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : NotificationInitializer {

  override fun initialize() {
    NotificationChannelType.entries.forEach(context::ensureNotificationChannel)
  }
}
