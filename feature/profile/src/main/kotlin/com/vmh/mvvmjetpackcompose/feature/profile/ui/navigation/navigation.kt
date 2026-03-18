package com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vmh.mvvmjetpackcompose.feature.profile.ui.ProfileRoute

const val ProfileGraphRoutePattern = "profile_graph"

const val ProfileRoutePattern = "profile_route"

fun NavController.navigateToProfileScreen(navOptions: NavOptions? = null) = navigate(
  route = ProfileRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.profileGraph() {
  navigation(
    route = ProfileGraphRoutePattern,
    startDestination = ProfileRoutePattern,
  ) {
    composable(route = ProfileRoutePattern) {
      ProfileRoute()
    }
  }
}
