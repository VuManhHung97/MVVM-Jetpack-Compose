package com.vmh.mvvmjetpackcompose.feature.dashboard.presentation.dashboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vmh.mvvmjetpackcompose.feature.dashboard.ui.dashboard.DashboardRoute

const val DashboardGraphRoutePattern = "dashboard_graph"

const val DashboardRoutePattern = "dashboard_route"

fun NavController.navigateToDashboardScreen(navOptions: NavOptions? = null) = navigate(
  route = DashboardRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.dashboardGraph(onNavigateToHistory: () -> Unit) {
  navigation(
    route = DashboardGraphRoutePattern,
    startDestination = DashboardRoutePattern,
  ) {
    composable(route = DashboardRoutePattern) {
      DashboardRoute(onNavigateToHistory = onNavigateToHistory)
    }
  }
}
