package com.vmh.mvvmjetpackcompose.feature.home.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.home.ui.HomeRoute
import com.vmh.mvvmjetpackcompose.feature.search.ui.navigation.navigateToSearch

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
  entry<HomeNavKey> {
    HomeRoute(onNavigateToSearchScreen = navigator::navigateToSearch)
  }
}
