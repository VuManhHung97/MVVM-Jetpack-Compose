package com.vmh.mvvmjetpackcompose

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.WebViewArgs
import com.vmh.mvvmjetpackcompose.ui.widget.navigation.MoshiNavType
import javax.inject.Inject

@Suppress("UseDataClass") // Not a pure data holder class.
@Immutable
internal class NavTypeContainer @Inject constructor(internal val webViewArgsNavType: MoshiNavType<WebViewArgs>)
