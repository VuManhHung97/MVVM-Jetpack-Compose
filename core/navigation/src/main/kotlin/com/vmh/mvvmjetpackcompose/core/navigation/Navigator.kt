package com.vmh.mvvmjetpackcompose.core.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(val state: AppNavigationState) {
  fun navigate(navKey: NavKey) {
    val tabs = state.tabs
    when {
      navKey in tabs.backStacks.keys -> tabs.topLevelRoute = navKey
      state.isInAppArea -> tabs.currentStack.add(navKey)
      else -> state.rootStack.add(navKey)
    }
  }

  fun resetRootTo(navKey: NavKey) {
    require(navKey !in state.tabs.backStacks.keys) {
      "resetRootTo is not key tabs: $navKey"
    }

    state.tabs.backStacks.forEach { (topLevelKey, stack) ->
      stack.clear()
      stack.add(topLevelKey)
    }
    state.tabs.topLevelRoute = state.tabs.startRoute
    state.rootStack.apply {
      clear()
      add(navKey)
    }
  }

  fun goBack(): Boolean {
    if (!state.isInAppArea) {
      if (state.rootStack.size <= 1) return false
      state.rootStack.removeLastOrNull()
      return true
    }

    val tabs = state.tabs
    return when {
      tabs.currentStack.size > 1 -> {
        tabs.currentStack.removeLastOrNull()
        true
      }

      tabs.topLevelRoute != tabs.startRoute -> {
        tabs.topLevelRoute = tabs.startRoute
        true
      }

      else -> false
    }
  }

  fun clearCurrentStack() {
    state.tabs.currentStack.run {
      if (size > 1) subList(1, size).clear()
    }
  }
}
