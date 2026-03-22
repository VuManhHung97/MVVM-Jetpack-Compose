package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.SignUpScreen

const val SignUpRoutePattern = "sign_up_route"

fun NavController.navigateToSignUpScreen(navOptions: NavOptions? = null) = navigate(
  route = SignUpRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.signUpScreen(onNavigateBack: () -> Unit) {
  composable(route = SignUpRoutePattern) {
    SignUpScreen(
      onNavigateBack = onNavigateBack,
    )
  }
}
