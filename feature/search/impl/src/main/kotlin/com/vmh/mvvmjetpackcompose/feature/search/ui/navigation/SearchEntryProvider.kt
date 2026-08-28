package com.vmh.mvvmjetpackcompose.feature.search.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchRoute

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
  entry<SearchNavKey> {
    SearchRoute(
      onNavigateBack = navigator::goBack,
    )
  }
}
