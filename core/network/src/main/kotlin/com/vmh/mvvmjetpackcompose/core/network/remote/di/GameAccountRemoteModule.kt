package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.GameAccountRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl.FakeGameAccountRemoteDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.network.remote.service.GameAccountApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal interface GameAccountRemoteModule {
  @Binds
  fun gameAccountRemoteDataSource(impl: FakeGameAccountRemoteDataSourceImpl): GameAccountRemoteDataSource

  companion object {
    @Provides
    @Singleton
    fun gameAccountApiService(@SharedRetrofit retrofit: Retrofit): GameAccountApiService =
      GameAccountApiService(retrofit)
  }
}
