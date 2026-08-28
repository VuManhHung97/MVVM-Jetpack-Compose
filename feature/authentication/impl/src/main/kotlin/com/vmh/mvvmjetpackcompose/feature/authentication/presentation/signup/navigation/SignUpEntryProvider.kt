package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.AuthenticationNavKey
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.SignUpScreen

fun EntryProviderScope<NavKey>.signUpEntry(navigator: Navigator) {
  entry<SignUpNavKey> {
    SignUpScreen(
      onNavigateBack = navigator::goBack,
      navigateToAuthenticationScreen = { navigator.resetRootTo(AuthenticationNavKey) },
    )
  }
}
