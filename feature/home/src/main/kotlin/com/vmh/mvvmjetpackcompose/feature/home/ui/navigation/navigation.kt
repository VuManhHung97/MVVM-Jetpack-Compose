package com.vmh.mvvmjetpackcompose.feature.home.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vmh.mvvmjetpackcompose.feature.home.ui.HomeRoute

const val HomeGraphRoutePattern = "home_graph"

const val HomeRoutePattern = "home_route"

fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) = navigate(
  route = HomeRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.homeGraph() {
  navigation(
    route = HomeGraphRoutePattern,
    startDestination = HomeRoutePattern,
  ) {
    composable(route = HomeRoutePattern) {
      HomeRoute()
    }
  }
}
