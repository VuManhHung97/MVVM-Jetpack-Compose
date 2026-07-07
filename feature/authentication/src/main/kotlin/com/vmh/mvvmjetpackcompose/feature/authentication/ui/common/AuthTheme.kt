package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

/**
 * Illustration colors from the design handoff that have no Material [androidx.compose.material3.ColorScheme]
 * slot (mascot, background gradient, the button's 3D lip, cheeks). The Material roles (primary /
 * secondary / tertiary / surface / outline / error) live in the app light scheme; these decorative
 * values are kept here with the auth theme so the shared palette stays free of one-off tokens.
 */
@Immutable
internal data class AuthExtendedColors(
  val backgroundTop: Color,
  val backgroundMid: Color,
  val backgroundBottom: Color,
  val ctaGradientTop: Color,
  val ctaShadow: Color,
  val focusAmber: Color,
  val matchBorder: Color,
  val mascotBase: Color,
  val mascotHighlight: Color,
  val mascotGlow: Color,
  val mascotEye: Color,
  val mascotLeafLight: Color,
  val mascotLeafDark: Color,
  val cheek: Color,
)

// Standard Material 3 "extended color" pattern: theme values with no ColorScheme slot are handed
// down a CompositionLocal, exactly like MaterialTheme's own colors.
@Suppress("CompositionLocalAllowlist")
internal val LocalAuthExtendedColors = staticCompositionLocalOf<AuthExtendedColors> {
  error("AuthExtendedColors not provided. Wrap the screen in AuthTheme { }.")
}

/** Convenience accessor mirroring [androidx.compose.material3.MaterialTheme]. */
internal object AuthTheme {
  val extendedColors: AuthExtendedColors
    @Composable
    get() = LocalAuthExtendedColors.current
}

/**
 * Applies the auth Material palette (the app light scheme) plus the screen's own [extendedColors]
 * (declared in that screen's package, as they are used by a single screen only). Auth screens are
 * the only place the app renders in its light scheme.
 */
@Composable
internal fun AuthTheme(extendedColors: AuthExtendedColors, content: @Composable () -> Unit) {
  MVVMJetpackComposeTheme(useDarkTheme = false) {
    CompositionLocalProvider(LocalAuthExtendedColors provides extendedColors) {
      content()
    }
  }
}
