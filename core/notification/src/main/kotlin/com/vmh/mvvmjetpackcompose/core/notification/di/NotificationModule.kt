package com.vmh.mvvmjetpackcompose.core.notification.di

import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.vmh.mvvmjetpackcompose.core.notification.NotificationInitializer
import com.vmh.mvvmjetpackcompose.core.notification.NotificationTokenProvider
import com.vmh.mvvmjetpackcompose.core.notification.Notifier
import com.vmh.mvvmjetpackcompose.core.notification.internal.DefaultNotificationInitializer
import com.vmh.mvvmjetpackcompose.core.notification.internal.DefaultNotificationTokenProvider
import com.vmh.mvvmjetpackcompose.core.notification.internal.DefaultNotifier
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NotificationModule {
  @Binds
  @Singleton
  fun bindNotifier(impl: DefaultNotifier): Notifier

  @Binds
  @Singleton
  fun bindNotificationTokenProvider(impl: DefaultNotificationTokenProvider): NotificationTokenProvider

  @Binds
  @Singleton
  fun bindNotificationInitializer(impl: DefaultNotificationInitializer): NotificationInitializer

  companion object {
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = Firebase.messaging
  }
}
