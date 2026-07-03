package com.vmh.mvvmjetpackcompose.core.notification

import com.vmh.mvvmjetpackcompose.core.notification.model.PushMessage

/**
 * Posts system notifications to the status bar.
 *
 * Fire-and-forget by design — displaying a notification must never block or fail a business flow,
 * so there is no [kotlin.Result] here. If notifications are disabled at the OS level (permission
 * denied on Android 13+), the call is silently a no-op.
 */
interface Notifier {
  /**
   * Displays [message] as a system notification on its [PushMessage.channelType] channel.
   *
   * Does nothing for a data-only message (no title and no body) — such messages are meant to be
   * handled silently by the app rather than shown to the user.
   */
  fun showNotification(message: PushMessage)
}
