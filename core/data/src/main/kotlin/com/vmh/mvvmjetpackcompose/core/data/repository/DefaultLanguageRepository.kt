package com.vmh.mvvmjetpackcompose.core.data.repository

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.data.mapper.language.toLanguage
import com.vmh.mvvmjetpackcompose.core.domain.repository.LanguageRepository
import com.vmh.mvvmjetpackcompose.core.local.datasource.LanguageLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.datasource.LocaleLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.mapper.toLocalLocale
import com.vmh.mvvmjetpackcompose.core.local.mapper.toLocale
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.language.Language
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.LanguageRemoteDataSource
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class DefaultLanguageRepository @Inject constructor(
  private val languageRemoteDataSource: LanguageRemoteDataSource,
  private val languageLocalDataSource: LanguageLocalDataSource,
  private val localeLocalDataSource: LocaleLocalDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : LanguageRepository {

  override suspend fun getLanguages(): Result<List<Language>, AppError> = withContext(appCoroutineDispatchers.io) {
    languageLocalDataSource.observe().first()?.let { return@withContext Ok(it) }
    coroutineBinding {
      languageRemoteDataSource.getLanguages()
        .bind()
        .map { it.toLanguage() }
        .also(languageLocalDataSource::update)
    }
  }

  override fun observeCurrentLocale() = localeLocalDataSource
    .observe()
    .map { result ->
      result.map { it.toLocale() }
    }
    .distinctUntilChanged()

  override suspend fun setCurrentLocale(locale: Locale) = withContext(appCoroutineDispatchers.io) {
    localeLocalDataSource.update(locale.toLocalLocale())
  }
}

suspend fun LanguageRepository.getCurrentLocale(): Locale = observeCurrentLocale()
  .first()
  .fold(
    success = { it ?: LanguageRepository.DEFAULT_LOCALE },
    failure = { LanguageRepository.DEFAULT_LOCALE },
  )
