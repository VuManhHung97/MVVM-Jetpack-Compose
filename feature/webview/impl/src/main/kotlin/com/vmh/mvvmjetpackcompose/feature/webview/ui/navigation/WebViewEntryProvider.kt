package com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.webview.ui.WebViewRoute

fun EntryProviderScope<NavKey>.webViewEntry(navigator: Navigator) {
  entry<WebViewNavKey> { key ->
    WebViewRoute(
      key = key,
      onNavigateBack = navigator::goBack,
    )
  }
}
