package com.vmh.mvvmjetpackcompose.core.network.remote.di

import javax.inject.Qualifier

// --------------------------------- Refresh ---------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class RefreshApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class RefreshOkHttpClient

// --------------------------------- Shared ---------------------------------

@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class SharedRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class SharedOkHttpClient
