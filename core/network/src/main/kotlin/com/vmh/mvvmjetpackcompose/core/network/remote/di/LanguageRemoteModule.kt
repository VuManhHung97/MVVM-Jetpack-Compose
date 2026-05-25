package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.LanguageRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl.FakeLanguageRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface LanguageRemoteModule {
  @Binds
  fun languageRemoteDataSource(impl: FakeLanguageRemoteDataSourceImpl): LanguageRemoteDataSource
}
