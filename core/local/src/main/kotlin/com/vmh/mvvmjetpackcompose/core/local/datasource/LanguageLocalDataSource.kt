package com.vmh.mvvmjetpackcompose.core.local.datasource

import com.vmh.mvvmjetpackcompose.core.model.language.Language
import kotlinx.coroutines.flow.Flow

interface LanguageLocalDataSource {
  fun observe(): Flow<List<Language>?>
  fun update(languages: List<Language>)
}
