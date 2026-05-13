package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.vmh.mvvmjetpackcompose.core.network.remote.service.RefreshTokenApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal interface RefreshRemoteModule {
  companion object {
    @Provides
    @Singleton
    fun refreshTokenApiService(@RefreshApiRetrofit retrofit: Retrofit): RefreshTokenApiService =
      RefreshTokenApiService(retrofit)
  }
}
