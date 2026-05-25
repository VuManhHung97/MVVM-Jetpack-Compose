package com.vmh.mvvmjetpackcompose.locale

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.core.os.LocaleListCompat
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.vmh.mvvmjetpackcompose.BuildConfig
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineScope
import com.vmh.mvvmjetpackcompose.core.data.repository.getCurrentLocale
import com.vmh.mvvmjetpackcompose.core.domain.repository.LanguageRepository
import com.vmh.mvvmjetpackcompose.core.model.language.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalAtomicApi::class)
@Singleton
internal class LocaleInitializer @Inject constructor(
  private val appCoroutineScope: AppCoroutineScope,
  private val languageRepository: LanguageRepository,
  @param:ApplicationContext private val appContext: Context,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) {
  private val isSyncing = AtomicBoolean(false)
  private val initializationComplete = CompletableDeferred<Unit>()

  fun initialize() {
    // Initialize in the main thread as soon as possible.
    appCoroutineScope.launch(
      context = appCoroutineDispatchers.immediateMain,
      start = CoroutineStart.UNDISPATCHED,
    ) {
      // Read the current locale once to trigger the initialization
      val current = languageRepository.getCurrentLocale()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        resyncFromSystemInternal(current)
      }
      initializationComplete.complete(Unit)
    }
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  suspend fun resyncFromSystemIfNeeded() {
    initializationComplete.await()
    resyncFromSystemInternal(languageRepository.getCurrentLocale())
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  private suspend fun resyncFromSystemInternal(current: Locale) {
    val canContinue = isSyncing.compareAndSet(expectedValue = false, newValue = true)

    if (!canContinue) {
      if (BuildConfig.DEBUG) {
        Timber.d("LocaleController.resyncFromSystemIfNeeded: already syncing, skip")
      }
      return
    }

    val appLocales = appContext.readSystemPerAppLocales()
    val appLocale = appLocales[0]?.stripExtensions()

    val candidate = if (
      appLocale != null &&
      current.toLanguageTag() != appLocale.toLanguageTag()
    ) {
      appLocale
    } else {
      current
    }

    coroutineBinding {
      val languages = languageRepository.getLanguages().bind()
      val locale = findBestMatchingLocale(candidate, languages) ?: LanguageRepository.DEFAULT_LOCALE

      if (BuildConfig.DEBUG) {
        Timber.d(
          "LocaleController.resyncFromSystemIfNeeded: " +
            "appLocales=$appLocales, " +
            "systemLocales=$appLocale, " +
            "current=$current, " +
            "candidate=$candidate, " +
            "-> locale=$locale",
        )
      }

      languageRepository.setCurrentLocale(locale).bind()
    }.onFailure { Timber.e(it, "LocaleController.resyncFromSystemIfNeeded failed") }
      .onSuccess { Timber.d("LocaleController.resyncFromSystemIfNeeded succeeded: $it") }

    isSyncing.store(false)
  }

  /**
   * Finds the best matching locale from available languages.
   *
   * @param candidate The candidate locale to match against available languages
   * @param languages List of available languages from the repository
   * @return Best matching locale or null if no match found
   */
  private fun findBestMatchingLocale(candidate: Locale, languages: List<Language>): Locale? {
    val candidateLanguage = candidate.language
    val candidateLanguageTag = candidate.toLanguageTag()

    // First priority: exact match (language + country)
    val hasExactMatch = languages.any { language ->
      language.languageCode.tag == candidateLanguageTag
    }
    if (hasExactMatch) {
      return candidate
    }

    // Second priority: language-only match (ignore country)
    val hasLanguageOnlyMatch = languages.any { language ->
      language.languageCode.tag == candidateLanguage
    }
    if (hasLanguageOnlyMatch) {
      return Locale(candidateLanguage)
    }

    return null
  }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal fun Context.readSystemPerAppLocales(): LocaleListCompat {
  val localeManager = getSystemService<LocaleManager>()
    ?: return LocaleListCompat.getEmptyLocaleList()
  return LocaleListCompat.wrap(localeManager.applicationLocales)
}
