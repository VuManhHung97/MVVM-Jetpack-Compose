package com.vmh.mvvmjetpackcompose.feature.webview.ui

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.vmh.mvvmjetpackcompose.feature.webview.ui.di.SharedWebViewBaseUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@HiltViewModel
internal class WebViewViewModel @Inject constructor(@param:SharedWebViewBaseUrl val webViewBaseUrl: String) :
  ViewModel() {
  private val trustedToPrivateDomain: String? = webViewBaseUrl
    .toHttpUrlOrNull()
    ?.topPrivateDomain()

  private val trustedSchemes = setOf("https")

  internal fun isUrlTrusted(url: String): Boolean {
    if (trustedToPrivateDomain == null || url.isBlank()) return false

    val httpUrl = url.toHttpUrlOrNull()
      ?.takeIf { it.scheme.lowercase() in trustedSchemes }
      ?: return false

    return httpUrl.topPrivateDomain() == trustedToPrivateDomain
  }

  internal fun getUrl(path: String): String = webViewBaseUrl.toUri()
    .buildUpon()
    .appendPath(path)
    .build()
    .toString()
}
