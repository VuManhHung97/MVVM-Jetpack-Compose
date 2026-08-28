package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation.navigateToSignIn
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.authentication.AuthenticationRoute
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainNavKey

fun EntryProviderScope<NavKey>.authenticationEntry(navigator: Navigator) {
  entry<AuthenticationNavKey> {
    AuthenticationRoute(
      onNavigateToSignInScreen = navigator::navigateToSignIn,
      onNavigateToHomeScreen = { navigator.resetRootTo(MainNavKey) },
    )
  }
}
