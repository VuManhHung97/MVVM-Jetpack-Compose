package com.vmh.mvvmjetpackcompose.core.network.remote.di

import javax.inject.Qualifier

// --------------------------------- Auth ---------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class AuthApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class AuthOkHttpClient

// --------------------------------- Shared ---------------------------------

@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class SharedRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class SharedOkHttpClient
