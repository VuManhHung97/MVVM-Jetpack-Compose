package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.ForceUpdateErrorEventDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.ForceUpdateErrorEventEmit
import com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl.RealForceUpdateErrorEventHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ForceUpdateErrorEventModule {
  @Binds
  @Singleton
  fun bindForceUpdateErrorEventDataSource(impl: RealForceUpdateErrorEventHandler): ForceUpdateErrorEventDataSource

  @Binds
  @Singleton
  fun bindForceUpdateErrorEventEmit(impl: RealForceUpdateErrorEventHandler): ForceUpdateErrorEventEmit
}
