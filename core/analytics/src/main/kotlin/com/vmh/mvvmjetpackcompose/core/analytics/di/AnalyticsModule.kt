package com.vmh.mvvmjetpackcompose.core.analytics.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsTracker
import com.vmh.mvvmjetpackcompose.core.analytics.internal.FirebaseAnalyticsTracker
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface AnalyticsModule {
  @Binds
  @Singleton
  fun bindAnalyticsTracker(impl: FirebaseAnalyticsTracker): AnalyticsTracker

  companion object {
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics
  }
}
