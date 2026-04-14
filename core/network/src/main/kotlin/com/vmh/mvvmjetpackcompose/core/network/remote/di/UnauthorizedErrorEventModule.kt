package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UnauthorizedErrorEventDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UnauthorizedErrorEventEmit
import com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl.RealUnauthorizedErrorEventHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UnauthorizedErrorEventModule {
  @Binds
  @Singleton
  fun bindUnauthorizedErrorEventDataSource(impl: RealUnauthorizedErrorEventHandler): UnauthorizedErrorEventDataSource

  @Binds
  @Singleton
  fun bindUnauthorizedErrorEventEmit(impl: RealUnauthorizedErrorEventHandler): UnauthorizedErrorEventEmit
}
