package com.vmh.mvvmjetpackcompose.feature.webview.ui.di

import com.vmh.mvvmjetpackcompose.feature.webview.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface WebViewModule {
  companion object {
    @Provides
    @SharedWebViewBaseUrl
    fun sharedWebViewBaseUrl(): String = BuildConfig.WEB_VIEW_BASE_URL
  }
}
