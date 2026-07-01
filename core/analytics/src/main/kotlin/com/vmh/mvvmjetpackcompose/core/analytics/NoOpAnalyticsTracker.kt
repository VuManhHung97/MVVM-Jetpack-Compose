package com.vmh.mvvmjetpackcompose.core.analytics

/**
 * A no-op [AnalyticsTracker] — used for unit tests, Compose `@Preview`, or builds that want to
 * disable analytics entirely. It has no dependency on any SDK.
 */
object NoOpAnalyticsTracker : AnalyticsTracker {
  override fun logEvent(event: AnalyticsEvent) = Unit

  override fun setUserId(userId: String?) = Unit

  override fun setUserProperty(name: String, value: String?) = Unit

  override fun setCollectionEnabled(isEnabled: Boolean) = Unit
}
