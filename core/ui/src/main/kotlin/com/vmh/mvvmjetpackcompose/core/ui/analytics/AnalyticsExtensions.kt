package com.vmh.mvvmjetpackcompose.core.ui.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsEvent
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsEvent.Param
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsEvent.ParamKey
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsEvent.Type
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsTracker
import com.vmh.mvvmjetpackcompose.core.analytics.NoOpAnalyticsTracker

/**
 * CompositionLocal that exposes the app-wide [AnalyticsTracker] to any Composable without drilling
 * it through every function.
 *
 * Provide the real tracker once at the app root (see `MainActivity`); previews and tests fall back
 * to [NoOpAnalyticsTracker], so a screen is always safe to compose in isolation.
 *
 * This lives in `core:ui` (not `core:analytics`) on purpose: it depends on Compose, whereas
 * `core:analytics` is kept Compose-free so it can be used from any layer.
 */
val LocalAnalyticsTracker = staticCompositionLocalOf<AnalyticsTracker> { NoOpAnalyticsTracker }

/**
 * Logs a screen view event. Kept here, next to [TrackScreenViewEvent], because tracking "which
 * screen is shown" is a UI concern; other, non-Compose events use the helpers in `core:analytics`.
 */
fun AnalyticsTracker.logScreenView(screenName: String) {
  logEvent(
    AnalyticsEvent(
      type = Type.SCREEN_VIEW,
      params = listOf(Param(key = ParamKey.SCREEN_NAME, value = screenName)),
    ),
  )
}

/**
 * Logs a single screen-view event when this composable first enters the composition. Drop it at the
 * top of a screen composable — no ViewModel wiring required:
 *
 * ```
 * @Composable
 * fun SignInScreen(...) {
 *   TrackScreenViewEvent(screenName = "SignIn")
 *   ...
 * }
 * ```
 */
@Composable
fun TrackScreenViewEvent(screenName: String, analyticsTracker: AnalyticsTracker = LocalAnalyticsTracker.current) =
  DisposableEffect(Unit) {
    analyticsTracker.logScreenView(screenName)
    onDispose {}
  }
