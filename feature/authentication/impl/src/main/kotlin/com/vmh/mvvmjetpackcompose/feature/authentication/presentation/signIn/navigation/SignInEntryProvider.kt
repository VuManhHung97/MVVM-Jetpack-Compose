package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.AuthenticationNavKey
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation.navigateToSignUp
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.signin.SignInRoute

fun EntryProviderScope<NavKey>.signInEntry(navigator: Navigator) {
  entry<SignInNavKey> {
    SignInRoute(
      onNavigateBack = navigator::goBack,
      onNavigateToSignUpScreen = navigator::navigateToSignUp,
      navigateToAuthenticationScreen = { navigator.resetRootTo(AuthenticationNavKey) },
    )
  }
}
