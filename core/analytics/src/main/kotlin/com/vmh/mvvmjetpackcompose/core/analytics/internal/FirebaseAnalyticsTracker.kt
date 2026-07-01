package com.vmh.mvvmjetpackcompose.core.analytics.internal

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsEvent
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsTracker
import javax.inject.Inject

/**
 * An [AnalyticsTracker] implementation that forwards events to Firebase Analytics.
 *
 * This is the *only* place in the app that knows about the Firebase SDK — every other layer
 * sees just the [AnalyticsTracker] abstraction. The class is `internal` so it cannot be used
 * outside this module.
 */
internal class FirebaseAnalyticsTracker @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) :
  AnalyticsTracker {

  override fun logEvent(event: AnalyticsEvent) {
    firebaseAnalytics.logEvent(event.type.take(AnalyticsEvent.MAX_TYPE_LENGTH)) {
      event.params.forEach { param ->
        // Firebase rejects the event if a key/value exceeds the limit — trim so we don't drop it.
        param(
          param.key.take(AnalyticsEvent.MAX_PARAM_KEY_LENGTH),
          param.value.take(AnalyticsEvent.MAX_PARAM_VALUE_LENGTH),
        )
      }
    }
  }

  override fun setUserId(userId: String?) {
    firebaseAnalytics.setUserId(userId)
  }

  override fun setUserProperty(name: String, value: String?) {
    firebaseAnalytics.setUserProperty(name.take(AnalyticsEvent.MAX_PARAM_KEY_LENGTH), value)
  }

  override fun setCollectionEnabled(isEnabled: Boolean) {
    firebaseAnalytics.setAnalyticsCollectionEnabled(isEnabled)
  }
}
