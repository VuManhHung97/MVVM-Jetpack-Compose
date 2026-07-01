package com.vmh.mvvmjetpackcompose.core.analytics

import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsEvent.Param
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsEvent.ParamKey
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsEvent.Type

/**
 * Helpers for the most common non-Compose events — they keep call sites short, give parameters
 * clear names, and avoid repeatedly typing the [Type] / [ParamKey] constants. Call these from a
 * ViewModel with an injected [AnalyticsTracker].
 *
 * The screen-view helper lives in `core:ui` instead (paired with `TrackScreenViewEvent`), since
 * "which screen is shown" is a Compose/UI concern.
 */

/** Logs selecting a piece of content (e.g. opening an item in a list). */
fun AnalyticsTracker.logSelectContent(contentType: String, itemId: String) {
  logEvent(
    AnalyticsEvent(
      type = Type.SELECT_CONTENT,
      params = listOf(
        Param(key = ParamKey.CONTENT_TYPE, value = contentType),
        Param(key = ParamKey.ITEM_ID, value = itemId),
      ),
    ),
  )
}

/** Logs a keyword search. */
fun AnalyticsTracker.logSearch(searchTerm: String) {
  logEvent(
    AnalyticsEvent(
      type = Type.SEARCH,
      params = listOf(Param(key = ParamKey.SEARCH_TERM, value = searchTerm)),
    ),
  )
}

/** Logs a successful login. */
fun AnalyticsTracker.logLogin(method: String) {
  logEvent(
    AnalyticsEvent(
      type = Type.LOGIN,
      params = listOf(Param(key = ParamKey.METHOD, value = method)),
    ),
  )
}

/** Logs a successful sign-up. */
fun AnalyticsTracker.logSignUp(method: String) {
  logEvent(
    AnalyticsEvent(
      type = Type.SIGN_UP,
      params = listOf(Param(key = ParamKey.METHOD, value = method)),
    ),
  )
}
