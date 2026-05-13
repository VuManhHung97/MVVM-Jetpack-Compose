package com.vmh.mvvmjetpackcompose.core.common.coroutine

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zacsweers.moshix.sealed.annotations.NestedSealed
import dev.zacsweers.moshix.sealed.reflect.MoshiSealedJsonAdapterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface CoreModule {
  @Binds
  @Singleton
  fun appCoroutineDispatchers(impl: DefaultAppCoroutineDispatchers): AppCoroutineDispatchers

  @Binds
  @Singleton
  fun appCoroutineScope(impl: DefaultAppCoroutineScope): AppCoroutineScope

  companion object {
    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi
      .Builder()
      .add(MoshiSealedJsonAdapterFactory())
      .add(NestedSealed.Factory())
      .addLast(KotlinJsonAdapterFactory())
      .build()
  }
}
