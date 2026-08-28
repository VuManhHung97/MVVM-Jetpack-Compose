package com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.AuthenticationNavKey
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.navigation.navigateToLanguage
import com.vmh.mvvmjetpackcompose.feature.profile.ui.ProfileRoute
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.navigateToWebView

fun EntryProviderScope<NavKey>.profileEntry(navigator: Navigator) {
  entry<ProfileNavKey> {
    ProfileRoute(
      onNavigateToLanguageScreen = navigator::navigateToLanguage,
      onNavigateToAuthenticationScreen = { navigator.resetRootTo(AuthenticationNavKey) },
      onNavigateToWebViewScreen = { destination ->
        navigator.navigateToWebView(destination = destination)
      },
    )
  }
}
