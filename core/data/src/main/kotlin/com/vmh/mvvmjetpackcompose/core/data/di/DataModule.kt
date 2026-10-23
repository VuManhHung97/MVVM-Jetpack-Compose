package com.vmh.mvvmjetpackcompose.core.data.di

import com.vmh.mvvmjetpackcompose.core.data.repository.DefaultAuthRepository
import com.vmh.mvvmjetpackcompose.core.data.repository.DefaultForceUpdateErrorEventRepository
import com.vmh.mvvmjetpackcompose.core.data.repository.DefaultLanguageRepository
import com.vmh.mvvmjetpackcompose.core.data.repository.DefaultSearchRepository
import com.vmh.mvvmjetpackcompose.core.data.repository.DefaultUnauthorizedErrorEventRepository
import com.vmh.mvvmjetpackcompose.core.data.repository.fcm.DefaultFcmTokenManager
import com.vmh.mvvmjetpackcompose.core.data.repository.fcm.SynchronizedFcmTokenManager
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.FcmTokenManager
import com.vmh.mvvmjetpackcompose.core.domain.repository.ForceUpdateErrorEventRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.LanguageRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.SearchRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.UnauthorizedErrorEventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
  @Binds
  fun authRepository(impl: DefaultAuthRepository): AuthRepository

  @Binds
  fun searchRepository(impl: DefaultSearchRepository): SearchRepository

  @Binds
  fun languageRepository(impl: DefaultLanguageRepository): LanguageRepository

  @Binds
  fun bindUnauthorizedErrorEventRepository(
    impl: DefaultUnauthorizedErrorEventRepository,
  ): UnauthorizedErrorEventRepository

  @Binds
  fun bindForceUpdateErrorEventRepository(impl: DefaultForceUpdateErrorEventRepository): ForceUpdateErrorEventRepository

  @Binds
  @DelegateFcmTokenManager
  fun bindDefaultFcmTokenManager(impl: DefaultFcmTokenManager): FcmTokenManager

  @Binds
  fun bindFcmTokenManager(impl: SynchronizedFcmTokenManager): FcmTokenManager
}
