package com.vmh.mvvmjetpackcompose.core.network.remote.di

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.vmh.mvvmjetpackcompose.core.common.BuildConfig
import com.vmh.mvvmjetpackcompose.core.network.remote.di.DataRemoteModule.Companion.OkHttpClientConfig.OK_HTTP_CLIENT_CONNECT_TIMEOUT
import com.vmh.mvvmjetpackcompose.core.network.remote.di.DataRemoteModule.Companion.OkHttpClientConfig.OK_HTTP_CLIENT_READ_TIMEOUT
import com.vmh.mvvmjetpackcompose.core.network.remote.di.DataRemoteModule.Companion.OkHttpClientConfig.OK_HTTP_CLIENT_WRITE_TIMEOUT
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.AuthInterceptor
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ForceUpdateInterceptor
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.NoAuthInterceptor
import com.vmh.mvvmjetpackcompose.core.network.remote.response.ErrorResponse
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.jvm.java
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal interface DataRemoteModule {
  @Binds
  @Singleton
  fun remoteErrorMapper(impl: RemoteErrorMapperImpl): RemoteErrorMapper

  companion object {
    private object OkHttpClientConfig {
      const val OK_HTTP_CLIENT_READ_TIMEOUT = 60L
      const val OK_HTTP_CLIENT_WRITE_TIMEOUT = 60L
      const val OK_HTTP_CLIENT_CONNECT_TIMEOUT = 60L
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Provides
    fun errorResponseJsonAdapter(moshi: Moshi): JsonAdapter<ErrorResponse> = moshi.adapter()

    @Singleton
    @Provides
    fun mapJsonAdapter(moshi: Moshi): JsonAdapter<Map<String, Any?>> = moshi.adapter(
      Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        Any::class.java,
      ),
    )

    // ------------------------------ Auth ------------------------------

    @Provides
    @Singleton
    @AuthApiRetrofit
    fun authApiRetrofit(@AuthOkHttpClient okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    @Singleton
    @Provides
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(
      httpLoggingInterceptor: HttpLoggingInterceptor,
      noAuthInterceptor: NoAuthInterceptor,
      forceUpdateInterceptor: ForceUpdateInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
      .readTimeout(OK_HTTP_CLIENT_READ_TIMEOUT, TimeUnit.SECONDS)
      .writeTimeout(OK_HTTP_CLIENT_WRITE_TIMEOUT, TimeUnit.SECONDS)
      .connectTimeout(OK_HTTP_CLIENT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
      // 2. NoAuth interceptor
      .addInterceptor(noAuthInterceptor)
      // 3. Force update interceptor
      .addInterceptor(forceUpdateInterceptor)
      // 5. Http logging interceptor
      .addNetworkInterceptor(httpLoggingInterceptor)
      .build()

    // ------------------------------ Shared ------------------------------

    @Singleton
    @Provides
    @SharedRetrofit
    fun sharedRetrofit(@SharedOkHttpClient okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    @Singleton
    @Provides
    @SharedOkHttpClient
    fun provideOkHttpClient(
      httpLoggingInterceptor: HttpLoggingInterceptor,
      authInterceptor: AuthInterceptor,
      forceUpdateInterceptor: ForceUpdateInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
      .readTimeout(OK_HTTP_CLIENT_READ_TIMEOUT, TimeUnit.SECONDS)
      .writeTimeout(OK_HTTP_CLIENT_WRITE_TIMEOUT, TimeUnit.SECONDS)
      .connectTimeout(OK_HTTP_CLIENT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
      // 2. Auth interceptor
      .addInterceptor(authInterceptor)
      // 3. Force update interceptor
      .addInterceptor(forceUpdateInterceptor)
      // 5. Http logging interceptor
      .addNetworkInterceptor(httpLoggingInterceptor)
      .build()

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
      val interceptor = HttpLoggingInterceptor()

      interceptor.level =
        if (BuildConfig.DEBUG) {
          HttpLoggingInterceptor.Level.BODY
        } else {
          HttpLoggingInterceptor.Level.NONE
        }
      return interceptor
    }
  }
}
