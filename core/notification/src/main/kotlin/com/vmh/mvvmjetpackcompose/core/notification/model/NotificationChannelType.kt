package com.vmh.mvvmjetpackcompose.core.notification.model

/**
 * The set of notification channels the app registers.
 *
 * Modelled as an enum (not a raw [String]) so channel ids stay stable and exhaustive across the
 * codebase. [NotificationInitializer][com.vmh.mvvmjetpackcompose.core.notification.NotificationInitializer]
 * creates one system channel per entry.
 *
 * @param id the stable channel id persisted by the system — never rename an existing value.
 * @param importance mirrors `NotificationManager.IMPORTANCE_*`; kept as a plain [Int] to avoid a
 *   framework import in this pure-Kotlin model.
 */
enum class NotificationChannelType(val id: String, val importance: Importance) {
  /** Catch-all channel; also the FCM default channel referenced from the manifest. */
  Default(id = "default", importance = Importance.Default),
  ;

  /** Channel importance, decoupled from the `NotificationManager.IMPORTANCE_*` constants. */
  enum class Importance {
    Low,
    Default,
    High,
  }
}
