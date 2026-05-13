package com.vmh.mvvmjetpackcompose.feature.webview.ui.di

import com.squareup.moshi.Moshi
import com.vmh.mvvmjetpackcompose.feature.webview.BuildConfig
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.WebViewArgs
import com.vmh.mvvmjetpackcompose.ui.widget.navigation.MoshiNavType
import com.vmh.mvvmjetpackcompose.ui.widget.navigation.moshiNavTypeOf
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

    @Provides
    fun provideWebViewArgsNavType(moshi: Moshi): MoshiNavType<WebViewArgs> = moshiNavTypeOf<WebViewArgs>(moshi)
  }
}
