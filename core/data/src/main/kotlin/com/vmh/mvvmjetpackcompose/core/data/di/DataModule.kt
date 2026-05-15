package com.vmh.mvvmjetpackcompose.core.data.di

import com.vmh.mvvmjetpackcompose.core.data.repository.DefaultAuthRepository
import com.vmh.mvvmjetpackcompose.core.data.repository.DefaultSearchRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.AuthRepository
import com.vmh.mvvmjetpackcompose.core.domain.repository.SearchRepository
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
}
