package com.vmh.mvvmjetpackcompose.feature.main.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.navigation.accountGraph
import com.vmh.mvvmjetpackcompose.feature.dashboard.presentation.dashboard.navigation.DashboardGraphRoutePattern
import com.vmh.mvvmjetpackcompose.feature.dashboard.presentation.dashboard.navigation.dashboardGraph
import com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation.profileGraph
import com.vmh.mvvmjetpackcompose.feature.transaction.presentation.transaction.navigation.transactionGraph

const val MainGraphRoutePattern = "main_graph"

fun NavController.navigateToMainGraph(navOptions: NavOptions? = null) = navigate(
  MainGraphRoutePattern,
  navOptions,
)

fun NavGraphBuilder.mainGraph(
  onNavigateToHistory: () -> Unit,
  onNavigateToAccountDetail: (accountId: String) -> Unit,
  onNavigateBack: () -> Unit,
  onLogout: () -> Unit,
) {
  navigation(
    route = MainGraphRoutePattern,
    startDestination = DashboardGraphRoutePattern,
  ) {
    dashboardGraph(onNavigateToHistory = onNavigateToHistory)

    accountGraph(
      onNavigateToAccountDetail = onNavigateToAccountDetail,
      onNavigateBack = onNavigateBack,
    )

    transactionGraph()

    profileGraph(onLogout = onLogout)
  }
}
