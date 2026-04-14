package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.AuthRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl.AuthRemoteDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.network.remote.service.AuthApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthRemoteModule {
  @Binds
  fun authRemoteDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

  companion object {
    @Provides
    @Singleton
    fun authService(@AuthApiRetrofit retrofit: Retrofit): AuthApiService = AuthApiService(retrofit)
  }
}
