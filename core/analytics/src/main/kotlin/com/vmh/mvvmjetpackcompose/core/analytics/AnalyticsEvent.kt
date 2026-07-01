package com.vmh.mvvmjetpackcompose.core.analytics

/**
 * A backend-agnostic analytics event (Firebase, Datadog...).
 *
 * Features only build and send an [AnalyticsEvent] through [AnalyticsTracker]; they do not
 * know where the event is ultimately delivered. This keeps the presentation layer decoupled
 * from any specific analytics SDK.
 *
 * @param type the event name, e.g. [Type.SCREEN_VIEW]. Keep it under [MAX_TYPE_LENGTH] characters.
 * @param params the parameters attached to the event.
 */
data class AnalyticsEvent(val type: String, val params: List<Param> = emptyList()) {
  /**
   * A key/value pair attached to an [AnalyticsEvent].
   *
   * @param key the parameter name, e.g. [ParamKey.SCREEN_NAME]. Keep it under [MAX_PARAM_KEY_LENGTH] characters.
   * @param value the parameter value. Keep it under [MAX_PARAM_VALUE_LENGTH] characters.
   */
  data class Param(val key: String, val value: String)

  /** Standard event types — mirror of Firebase's built-in events to avoid coupling to the SDK. */
  object Type {
    const val SCREEN_VIEW = "screen_view"
    const val SELECT_CONTENT = "select_content"
    const val SEARCH = "search"
    const val SHARE = "share"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
  }

  /** Standard parameter keys. */
  object ParamKey {
    const val SCREEN_NAME = "screen_name"
    const val CONTENT_TYPE = "content_type"
    const val ITEM_ID = "item_id"
    const val SEARCH_TERM = "search_term"
    const val METHOD = "method"
  }

  companion object {
    /** Firebase length limits — used to safely trim values in the implementation layer. */
    const val MAX_TYPE_LENGTH = 40
    const val MAX_PARAM_KEY_LENGTH = 40
    const val MAX_PARAM_VALUE_LENGTH = 100
  }
}
