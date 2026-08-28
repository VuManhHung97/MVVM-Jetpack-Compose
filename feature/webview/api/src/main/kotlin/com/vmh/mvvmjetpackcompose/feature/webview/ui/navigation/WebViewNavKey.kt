package com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class WebViewNavKey(val destination: WebViewDestination) : NavKey

fun Navigator.navigateToWebView(destination: WebViewDestination) = navigate(
  WebViewNavKey(destination = destination),
)
