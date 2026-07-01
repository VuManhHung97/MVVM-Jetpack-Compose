package com.vmh.mvvmjetpackcompose.core.analytics

/**
 * The single entry point for logging analytics across the app.
 *
 * This is the only public abstraction of the `core:analytics` module; every implementation
 * ([FirebaseAnalyticsTracker], [NoOpAnalyticsTracker]) is `internal`. Inject this interface
 * via Hilt into a ViewModel or any layer that needs to log events.
 *
 * The functions are intentionally fire-and-forget (not suspend, no [Result]): analytics is
 * best-effort and must never slow down or fail the business flow.
 */
interface AnalyticsTracker {
  /** Logs an [AnalyticsEvent] to the analytics backend. */
  fun logEvent(event: AnalyticsEvent)

  /**
   * Attaches a user id so that every subsequent event is attributed to this user.
   *
   * @param userId pass `null` to clear the association (e.g. after sign-out).
   */
  fun setUserId(userId: String?)

  /**
   * Sets a persistent user property (e.g. tier, language) used to segment reports.
   *
   * @param value pass `null` to clear the property.
   */
  fun setUserProperty(name: String, value: String?)

  /** Enables/disables analytics collection — used by the privacy consent screen. */
  fun setCollectionEnabled(isEnabled: Boolean)
}
