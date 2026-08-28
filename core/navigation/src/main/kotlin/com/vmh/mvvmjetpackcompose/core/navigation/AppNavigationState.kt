package com.vmh.mvvmjetpackcompose.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import kotlinx.collections.immutable.ImmutableSet

data class AppNavigationState(val appRootKey: NavKey, val rootStack: NavBackStack<NavKey>, val tabs: NavigationState) {
  val isInAppArea: Boolean
    get() = rootStack.lastOrNull() == appRootKey
}

@Composable
fun rememberAppNavigationState(
  appRootKey: NavKey,
  rootStartKey: NavKey,
  tabStartRoute: NavKey,
  topLevelRoutes: ImmutableSet<NavKey>,
): AppNavigationState {
  val rootStack = rememberNavBackStack(rootStartKey)
  val tabs = rememberNavigationState(
    startRoute = tabStartRoute,
    topLevelRoutes = topLevelRoutes,
  )

  return remember(appRootKey, tabs) {
    AppNavigationState(
      appRootKey = appRootKey,
      rootStack = rootStack,
      tabs = tabs,
    )
  }
}
