package com.vmh.mvvmjetpackcompose.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.vmh.mvvmjetpackcompose.core.ui.navigation.findParentNavGraph
import com.vmh.mvvmjetpackcompose.feature.home.ui.navigation.navigateToHomeScreen
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainTopScreenTopLevelDestination
import com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation.navigateToProfileScreen

@Composable
fun rememberMainState(navController: NavHostController): MainState =
  remember(navController) { MainState(navController) }

class MainState(private val navController: NavHostController) {
  private val navBackStackEntryState: State<NavBackStackEntry?>
    @Composable
    get() = navController.currentBackStackEntryAsState()

  val currentDestination: NavDestination?
    @Composable get() = navBackStackEntryState.value?.destination

  fun navigateToTopLevelDestination(targetTopLevelDestination: MainTopScreenTopLevelDestination): Boolean {
    val currentDestination = navController.currentDestination ?: return false
    val parentNavGraph = currentDestination.findParentNavGraph() ?: return false

    // handle re-selecting bottom bar items.
    if (currentDestination.isTopLevelDestinationInHierarchy(targetTopLevelDestination)) {
      return false
    }

    // handle switching between top-level destinations.
    val topLevelNavOptions = navOptions {
      popUpTo(parentNavGraph.id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
    when (targetTopLevelDestination) {
      MainTopScreenTopLevelDestination.Home -> navController.navigateToHomeScreen(topLevelNavOptions)
      MainTopScreenTopLevelDestination.Settings -> navController.navigateToProfileScreen(topLevelNavOptions)
    }

    return true
  }
}

internal fun NavDestination?.isTopLevelDestinationInHierarchy(destination: MainTopScreenTopLevelDestination): Boolean {
  this ?: return false
  return hierarchy.any { it.route == destination.graphRoutePattern }
}
