package com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebViewArgs(val path: String, val title: String) : Parcelable {
  companion object
}
