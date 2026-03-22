package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.authentication.AuthenticationRoute

const val AuthenticationRoutePattern = "authentication_route"

fun NavController.navigateToAuthenticationScreen(navOptions: NavOptions? = null) = navigate(
  route = AuthenticationRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.authenticationScreen(onNavigateToSignInScreen: () -> Unit) {
  composable(route = AuthenticationRoutePattern) {
    AuthenticationRoute(
      onNavigateToSignInScreen = onNavigateToSignInScreen,
    )
  }
}
