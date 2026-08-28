package com.vmh.mvvmjetpackcompose.feature.webview.ui

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.common.DebouncedClickable
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.WebViewDestination
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.WebViewNavKey
import com.vmh.mvvmjetpackcompose.ui.widget.common.ClosedIconButton

@StringRes
private fun WebViewDestination.titleResId(): Int = when (this) {
  WebViewDestination.FAQ -> R.string.web_view_faq_title
  WebViewDestination.PrivacyPolicy -> R.string.web_view_privacy_policy_title
  WebViewDestination.TermsOfUse -> R.string.web_view_term_of_use_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WebViewRoute(
  key: WebViewNavKey,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: WebViewViewModel = hiltViewModel(),
) {
  var isLoading by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier,
    containerColor = MVVMJetPackComposeColors.Neutral100,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(key.destination.titleResId()),
            style = MVVMJetpackComposeTheme.typography.textStyleXLargeBold,
          )
        },
        actions = { ClosedIconButton(onClose = { DebouncedClickable.onClick(onNavigateBack) }) },
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MVVMJetPackComposeColors.Neutral100)
        .padding(innerPadding),
    ) {
      AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->

          WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT,
            )

            webViewClient = object : WebViewClient() {
              override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                isLoading = true
              }

              override fun onPageFinished(view: WebView?, url: String?) {
                isLoading = false
              }
            }

            webChromeClient = WebChromeClient()

            val url = viewModel.getUrl(key.destination.path)
            if (viewModel.isUrlTrusted(url = url)) {
              settings.javaScriptEnabled = true
              loadUrl(url)
            }
          }
        },
      )

      if (isLoading) {
        LoadingIndicator(modifier = Modifier.align(Alignment.Center))
      }
    }
  }
}
