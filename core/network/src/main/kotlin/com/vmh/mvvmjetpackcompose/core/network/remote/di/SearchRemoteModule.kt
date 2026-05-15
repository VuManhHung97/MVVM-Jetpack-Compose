package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.SearchRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl.RealSearchRemoteDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.network.remote.service.SearchApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal interface SearchRemoteModule {
  @Binds
  fun searchRemoteDataSource(impl: RealSearchRemoteDataSourceImpl): SearchRemoteDataSource

  companion object {
    @Provides
    @Singleton
    fun searchApiService(@SharedRetrofit retrofit: Retrofit): SearchApiService = SearchApiService(retrofit)
  }
}
