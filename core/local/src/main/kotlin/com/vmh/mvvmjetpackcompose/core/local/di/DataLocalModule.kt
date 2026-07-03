package com.vmh.mvvmjetpackcompose.core.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineScope
import com.vmh.mvvmjetpackcompose.core.local.LocalFcmToken
import com.vmh.mvvmjetpackcompose.core.local.LocalLocale
import com.vmh.mvvmjetpackcompose.core.local.LocalUser
import com.vmh.mvvmjetpackcompose.core.local.datasource.AuthLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.datasource.FcmTokenLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.datasource.LanguageLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.datasource.LocaleLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.datasourceimpl.AuthLocalDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.local.datasourceimpl.FcmTokenLocalDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.local.datasourceimpl.LanguageLocalDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.local.datasourceimpl.LocaleLocalDataSourceImpl
import com.vmh.mvvmjetpackcompose.core.local.mapper.LocalErrorMapper
import com.vmh.mvvmjetpackcompose.core.local.mapper.LocalErrorMapperImpl
import com.vmh.mvvmjetpackcompose.core.local.model.serializer.getDefaultSerializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal interface DataLocalModule {
  @Binds
  @Singleton
  fun localErrorMapper(impl: LocalErrorMapperImpl): LocalErrorMapper

  @Binds
  @Singleton
  fun authLocalDataSource(impl: AuthLocalDataSourceImpl): AuthLocalDataSource

  @Binds
  @Singleton
  fun localeLocalDataSource(impl: LocaleLocalDataSourceImpl): LocaleLocalDataSource

  @Binds
  @Singleton
  fun languageLocalDataSource(impl: LanguageLocalDataSourceImpl): LanguageLocalDataSource

  @Binds
  @Singleton
  fun fcmTokenLocalDataSource(impl: FcmTokenLocalDataSourceImpl): FcmTokenLocalDataSource

  companion object {
    private const val LOCAL_USER_DATA_STORE_FILE_NAME = "local_user_data_store"
    private const val LOCAL_LOCALE_DATA_STORE_FILE_NAME = "local_locale_data_store"
    private const val LOCAL_FCM_TOKEN_DATA_STORE_FILE_NAME = "local_fcm_token_data_store"

    @Provides
    @Singleton
    fun localUserDataStore(
      @ApplicationContext context: Context,
      appCoroutineScope: AppCoroutineScope,
    ): DataStore<LocalUser> = DataStoreFactory.create(
      serializer = getDefaultSerializer<LocalUser>(),
      produceFile = { context.dataStoreFile(LOCAL_USER_DATA_STORE_FILE_NAME) },
      scope = appCoroutineScope,
    )

    @Provides
    @Singleton
    fun localLocaleDataStore(
      @ApplicationContext context: Context,
      appCoroutineScope: AppCoroutineScope,
    ): DataStore<LocalLocale> = DataStoreFactory.create(
      serializer = getDefaultSerializer<LocalLocale>(),
      produceFile = { context.dataStoreFile(LOCAL_LOCALE_DATA_STORE_FILE_NAME) },
      scope = appCoroutineScope,
    )

    @Provides
    @Singleton
    fun localFcmTokenDataStore(
      @ApplicationContext context: Context,
      appCoroutineScope: AppCoroutineScope,
    ): DataStore<LocalFcmToken> = DataStoreFactory.create(
      serializer = getDefaultSerializer<LocalFcmToken>(),
      produceFile = { context.dataStoreFile(LOCAL_FCM_TOKEN_DATA_STORE_FILE_NAME) },
      scope = appCoroutineScope,
    )
  }
}
