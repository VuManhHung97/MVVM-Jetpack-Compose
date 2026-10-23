package com.vmh.mvvmjetpackcompose.feature.language.presentation.language

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.language.Language
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.LanguageUiState.Content.LanguageUiItem
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface LanguageUiState {
  companion object {
    val initial: LanguageUiState get() = Loading
  }

  @Immutable
  data object Loading : LanguageUiState

  @Immutable
  data class Content(val languages: ImmutableList<LanguageUiItem>, val isSaveButtonEnabled: Boolean) : LanguageUiState {
    @Immutable
    data class LanguageUiItem(
      val id: Long,
      val languageCode: Language.LanguageCode,
      val originalName: String,
      val isSelected: Boolean,
    )
  }

  @Immutable
  data class Error(val error: AppError) : LanguageUiState
}

sealed interface LanguageSingleEvent {
  data object LanguageChangeSuccess : LanguageSingleEvent
  data class LanguageChangeFailure(val appError: AppError) : LanguageSingleEvent
}

internal fun Language.toLanguageUiItem(isSelected: Boolean): LanguageUiItem = LanguageUiItem(
  id = id,
  languageCode = languageCode,
  originalName = originalName,
  isSelected = isSelected,
)
