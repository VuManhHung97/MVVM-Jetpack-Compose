package com.vmh.mvvmjetpackcompose.locale

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.github.michaelbull.result.fold
import com.vmh.mvvmjetpackcompose.BuildConfig
import com.vmh.mvvmjetpackcompose.core.domain.repository.LanguageRepository
import com.vmh.mvvmjetpackcompose.library.flowext.select
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.get
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

internal class LocaleController @Inject constructor(
  private val languageRepository: LanguageRepository,
  @param:ApplicationContext private val appContext: Context,
  private val localeInitializer: LocaleInitializer,
) {
  /**
   * Initialize locale for API 33+ devices. Should be called in the [AppCompatActivity.onCreate],
   * after `super.onCreate()`.
   *
   * ```kotlin
   * super.onCreate(savedInstanceState)
   * localeController.initSince33(this)
   * ```
   */
  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  fun initSince33(appCompatActivity: AppCompatActivity) {
    // For API versions >= 33, AppCompatDelegate.setApplicationLocales should be called
    // after `super.onCreate()` in AppCompatActivity.onCreate.
    appCompatActivity.lifecycleScope.launch {
      localeInitializer.resyncFromSystemIfNeeded()
      observeCurrentLocaleInternal()
    }
  }

  private suspend fun observeCurrentLocaleInternal() {
    languageRepository
      .observeCurrentLocale()
      .select { result ->
        result.fold(
          success = { it ?: LanguageRepository.DEFAULT_LOCALE },
          failure = {
            Timber.e(it, "Failed to observeCurrentLocale -> fallback to ${LanguageRepository.DEFAULT_LOCALE}")
            LanguageRepository.DEFAULT_LOCALE
          },
        )
      }
      .distinctUntilChanged()
      .collect(::setAppLocale)
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun setAppLocale(requested: Locale) {
    debugLog("LocaleController.setAppLocale: START SET requested=$requested >>>")

    when {
      Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ->
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(requested))

      else -> {
        if (appContext.readSystemPerAppLocales()[0] != requested) {
          AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(requested))
        } else {
          debugLog("LocaleController.setAppLocale: SKIP SET, already set to $requested")
        }
      }
    }

    debugLog("LocaleController.setAppLocale: COMPLETE SET requested=$requested <<<")
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun debugLog(message: String) {
    if (BuildConfig.DEBUG) {
      Timber.d(message)
    }
  }

  @EntryPoint
  @InstallIn(SingletonComponent::class)
  internal interface LocaleControllerEntryPoint {
    val localeController: LocaleController
  }

  companion object {
    @JvmStatic
    fun fromApplication(application: Application): LocaleController = EntryPointAccessors
      .fromApplication<LocaleControllerEntryPoint>(application)
      .localeController
      .also {
        if (BuildConfig.DEBUG) {
          Timber.d("fromApplication: localeController=$it")
        }
      }
  }
}
