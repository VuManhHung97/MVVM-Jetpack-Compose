package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UserRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl.RealUserRemoteDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.network.remote.service.UserApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal interface UserRemoteModule {
  @Binds
  fun userRemoteDataSource(impl: RealUserRemoteDataSourceImpl): UserRemoteDataSource

  companion object {
    @Provides
    @Singleton
    fun userApiService(@SharedRetrofit retrofit: Retrofit): UserApiService = UserApiService(retrofit)
  }
}
