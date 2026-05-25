package com.vmh.mvvmjetpackcompose.core.local.datasourceimpl

import com.vmh.mvvmjetpackcompose.core.local.datasource.LanguageLocalDataSource
import com.vmh.mvvmjetpackcompose.core.model.language.Language
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class LanguageLocalDataSourceImpl @Inject constructor() : LanguageLocalDataSource {
  private val _cache = MutableStateFlow<List<Language>?>(null)

  override fun observe() = _cache.asStateFlow()

  override fun update(languages: List<Language>) {
    _cache.value = languages
  }
}
