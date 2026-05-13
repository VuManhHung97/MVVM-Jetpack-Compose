package com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.vmh.mvvmjetpackcompose.feature.webview.ui.WebViewRoute
import com.vmh.mvvmjetpackcompose.ui.widget.navigation.MoshiNavType
import com.vmh.mvvmjetpackcompose.ui.widget.navigation.NavRouteDefinition

private val WebViewRouteDefinition = NavRouteDefinition(
  baseRoute = "web_view_route",
  argsType = WebViewArgs::class,
)

fun NavController.navigateToWebViewScreen(
  args: WebViewArgs,
  navType: MoshiNavType<WebViewArgs>,
  navOptions: NavOptions? = null,
) = navigate(
  route = WebViewRouteDefinition.createRoute(args, navType),
  navOptions = navOptions,
)

fun NavGraphBuilder.webViewScreen(
  navType: MoshiNavType<WebViewArgs>,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
) = with(WebViewRouteDefinition) {
  composable(argsNavType = navType) { navBackStackEntry ->
    WebViewRoute(
      modifier = modifier,
      args = remember(navBackStackEntry) { requireArgs(navBackStackEntry) },
      onNavigateBack = onNavigateBack,
    )
  }
}
