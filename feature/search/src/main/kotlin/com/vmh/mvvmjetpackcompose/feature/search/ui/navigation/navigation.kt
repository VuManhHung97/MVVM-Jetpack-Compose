package com.vmh.mvvmjetpackcompose.feature.search.ui.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchRoute

const val SearchRoutePattern = "search_route"

fun NavController.navigateToSearchScreen(navOptions: NavOptions? = null) = navigate(
  route = SearchRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.searchScreen(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
  composable(route = SearchRoutePattern) {
    SearchRoute(
      modifier = modifier,
      onNavigateBack = onNavigateBack,
    )
  }
}
