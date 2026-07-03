package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.FcmTokenRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl.RealFcmTokenRemoteDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.network.remote.service.FcmTokenApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal interface FcmTokenRemoteModule {
  @Binds
  fun fcmTokenRemoteDataSource(impl: RealFcmTokenRemoteDataSourceImpl): FcmTokenRemoteDataSource

  companion object {
    @Provides
    @Singleton
    fun fcmTokenApiService(@SharedRetrofit retrofit: Retrofit): FcmTokenApiService = FcmTokenApiService(retrofit)
  }
}
