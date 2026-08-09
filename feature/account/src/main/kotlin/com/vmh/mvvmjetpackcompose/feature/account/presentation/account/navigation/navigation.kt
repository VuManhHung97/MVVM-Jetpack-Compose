package com.vmh.mvvmjetpackcompose.feature.account.presentation.account.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.vmh.mvvmjetpackcompose.feature.account.ui.accountDetail.AccountDetailRoute
import com.vmh.mvvmjetpackcompose.feature.account.ui.accounts.AccountsRoute

const val AccountGraphRoutePattern = "account_graph"

const val AccountsRoutePattern = "accounts_route"

const val ACCOUNT_ID_ARG = "accountId"

private const val AccountDetailBaseRoute = "account_detail_route"

const val AccountDetailRoutePattern = "$AccountDetailBaseRoute/{$ACCOUNT_ID_ARG}"

fun NavController.navigateToAccountsScreen(navOptions: NavOptions? = null) = navigate(
  route = AccountsRoutePattern,
  navOptions = navOptions,
)

fun NavController.navigateToAccountDetailScreen(accountId: String) = navigate(
  route = "$AccountDetailBaseRoute/$accountId",
)

fun NavGraphBuilder.accountGraph(onNavigateToAccountDetail: (accountId: String) -> Unit, onNavigateBack: () -> Unit) {
  navigation(
    route = AccountGraphRoutePattern,
    startDestination = AccountsRoutePattern,
  ) {
    composable(route = AccountsRoutePattern) {
      AccountsRoute(onNavigateToAccountDetail = onNavigateToAccountDetail)
    }
    composable(
      route = AccountDetailRoutePattern,
      arguments = listOf(navArgument(ACCOUNT_ID_ARG) { type = NavType.StringType }),
    ) {
      AccountDetailRoute(onNavigateBack = onNavigateBack)
    }
  }
}
