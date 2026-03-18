package com.vmh.mvvmjetpackcompose.feature.main.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.vmh.mvvmjetpackcompose.feature.home.ui.navigation.HomeGraphRoutePattern
import com.vmh.mvvmjetpackcompose.feature.home.ui.navigation.homeGraph
import com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation.profileGraph

const val MainGraphRoutePattern = "main_graph"

fun NavController.navigateToMainGraph(navOptions: NavOptions? = null) = navigate(
  MainGraphRoutePattern,
  navOptions,
)

fun NavGraphBuilder.mainGraph() {
  navigation(
    route = MainGraphRoutePattern,
    startDestination = HomeGraphRoutePattern,
  ) {
    homeGraph()

    profileGraph()
  }
}
