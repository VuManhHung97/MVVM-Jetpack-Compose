package com.vmh.mvvmjetpackcompose.feature.transaction.presentation.transaction.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vmh.mvvmjetpackcompose.feature.transaction.ui.transaction.TransactionRoute

const val TransactionGraphRoutePattern = "transaction_graph"

const val TransactionRoutePattern = "transaction_route"

fun NavController.navigateToTransactionScreen(navOptions: NavOptions? = null) = navigate(
  route = TransactionRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.transactionGraph() {
  navigation(
    route = TransactionGraphRoutePattern,
    startDestination = TransactionRoutePattern,
  ) {
    composable(route = TransactionRoutePattern) {
      TransactionRoute()
    }
  }
}
