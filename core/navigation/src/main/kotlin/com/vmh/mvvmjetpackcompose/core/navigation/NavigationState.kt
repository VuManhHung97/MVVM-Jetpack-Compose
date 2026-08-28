package com.vmh.mvvmjetpackcompose.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun rememberNavigationState(startRoute: NavKey, topLevelRoutes: ImmutableSet<NavKey>): NavigationState {
  val topLevelRoute = rememberSerializable(
    startRoute,
    topLevelRoutes,
    serializer = MutableStateSerializer(NavKeySerializer()),
  ) {
    mutableStateOf(startRoute)
  }
  val backStacks = topLevelRoutes.associateWith { topLevelKey -> rememberNavBackStack(topLevelKey) }

  return remember(startRoute, topLevelRoutes) {
    NavigationState(
      startRoute = startRoute,
      topLevelRoute = topLevelRoute,
      backStacks = backStacks,
    )
  }
}

class NavigationState(
  val startRoute: NavKey,
  topLevelRoute: MutableState<NavKey>,
  val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {

  var topLevelRoute: NavKey by topLevelRoute

  val currentStack: NavBackStack<NavKey>
    get() = backStacks[topLevelRoute] ?: error("Back stack for $topLevelRoute does not exist")

  val stacksInUse: List<NavKey>
    get() = if (topLevelRoute == startRoute) {
      listOf(startRoute)
    } else {
      listOf(startRoute, topLevelRoute)
    }
}

@Composable
fun AppNavigationState.toEntries(
  entryProvider: (navKey: NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
  val isInAppArea = isInAppArea

  val rootEntries = if (isInAppArea) {
    emptyList()
  } else {
    val rootDecorators = listOf(
      rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
      rememberViewModelStoreNavEntryDecorator<NavKey>(),
    )
    rememberDecoratedNavEntries(
      backStack = rootStack,
      entryDecorators = rootDecorators,
      entryProvider = entryProvider,
    )
  }

  val tabEntries = if (isInAppArea) tabs.toEntries(entryProvider) else emptyList()

  return (rootEntries + tabEntries).toMutableStateList()
}

@Composable
private fun NavigationState.toEntries(entryProvider: (navKey: NavKey) -> NavEntry<NavKey>): List<NavEntry<NavKey>> {
  val decoratedEntries = backStacks.mapValues { (_, stack) ->
    val decorators = listOf(
      rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
      rememberViewModelStoreNavEntryDecorator<NavKey>(),
    )
    rememberDecoratedNavEntries(
      backStack = stack,
      entryDecorators = decorators,
      entryProvider = entryProvider,
    )
  }

  return stacksInUse.flatMap { topLevelKey -> decoratedEntries[topLevelKey].orEmpty() }
}
