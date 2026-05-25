package com.vmh.mvvmjetpackcompose.feature.language.presentation.language.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vmh.mvvmjetpackcompose.feature.language.ui.language.LanguageRoute

const val LanguageRoutePattern = "language_route"

fun NavController.navigateToLanguageScreen(navOptions: NavOptions? = null) = navigate(
  route = LanguageRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.languageScreen(onNavigateBack: () -> Unit) {
  composable(route = LanguageRoutePattern) {
    LanguageRoute(onNavigateBack = onNavigateBack)
  }
}
