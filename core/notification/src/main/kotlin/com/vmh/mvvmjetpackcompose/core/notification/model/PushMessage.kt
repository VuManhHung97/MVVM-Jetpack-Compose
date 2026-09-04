package com.vmh.mvvmjetpackcompose.core.notification.model

/**
 * A backend-agnostic representation of an incoming push message.
 *
 * The `:core:notification` module maps a raw Firebase `RemoteMessage` into this pure-Kotlin model
 * so that no other layer ever touches the Firebase SDK. It carries everything needed to display a
 * notification and to route the user when the notification is tapped.
 *
 * @param title notification title; `null` for data-only messages that render no default UI.
 * @param body notification body text; `null` for data-only messages.
 * @param channelType the channel this message should be posted to.
 * @param deepLink an optional deep link (e.g. `dld://androidbase/...`) to open when tapped.
 * @param data the raw key/value data payload, forwarded verbatim for feature-specific handling.
 */
data class PushMessage(
  val title: String?,
  val body: String?,
  val channelType: NotificationChannelType = NotificationChannelType.Default,
  val deepLink: String? = null,
  val data: Map<String, String> = emptyMap(),
)
