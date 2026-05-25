package com.vmh.mvvmjetpackcompose.core.domain.repository

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.language.Language
import java.util.Locale
import kotlinx.coroutines.flow.Flow

interface LanguageRepository {
  suspend fun getLanguages(): Result<List<Language>, AppError>
  fun observeCurrentLocale(): Flow<Result<Locale?, AppError.LocalStorageException>>
  suspend fun setCurrentLocale(locale: Locale): Result<Unit, AppError.LocalStorageException>

  companion object {
    val DEFAULT_LOCALE: Locale get() = Locale.ENGLISH
  }
}
