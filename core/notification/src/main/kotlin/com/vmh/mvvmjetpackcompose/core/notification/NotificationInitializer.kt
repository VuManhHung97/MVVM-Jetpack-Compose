package com.vmh.mvvmjetpackcompose.core.notification

/**
 * Registers the app's notification channels with the system.
 *
 * Channels are idempotent, so this can be called on every launch. Invoke it once from
 * `Application.onCreate()` (same pattern as the locale initializer) to guarantee channels exist
 * before the first notification — including notifications posted by the OS from an FCM payload
 * while the app is in the background.
 */
interface NotificationInitializer {
  fun initialize()
}
