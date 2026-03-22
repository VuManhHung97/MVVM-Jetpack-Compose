package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.signin.SignInRoute

const val SignInRoutePattern = "sign_in_route"

fun NavController.navigateToSignInScreen(navOptions: NavOptions? = null) = navigate(
  route = SignInRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.signInScreen(onNavigateBack: () -> Unit, onNavigateToSignUpScreen: () -> Unit) {
  composable(route = SignInRoutePattern) {
    SignInRoute(
      onNavigateBack = onNavigateBack,
      onNavigateToSignUpScreen = onNavigateToSignUpScreen,
    )
  }
}
