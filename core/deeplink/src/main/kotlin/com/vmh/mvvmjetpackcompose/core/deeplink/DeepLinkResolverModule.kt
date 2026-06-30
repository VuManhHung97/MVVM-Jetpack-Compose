package com.vmh.mvvmjetpackcompose.core.deeplink

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DeepLinkResolverModule {
  @Binds
  @Singleton
  abstract fun bindDeepLinkResolver(impl: DefaultDeepLinkResolver): DeepLinkResolver
}
