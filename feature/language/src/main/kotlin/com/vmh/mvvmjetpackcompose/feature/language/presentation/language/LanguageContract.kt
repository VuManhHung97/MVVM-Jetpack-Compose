package com.vmh.mvvmjetpackcompose.feature.language.presentation.language

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.language.Language
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.LanguageUiState.Content.LanguageUiItem

@Immutable
sealed interface LanguageUiState {
  companion object {
    val initial: LanguageUiState get() = Loading
  }

  @Immutable
  data object Loading : LanguageUiState

  @Immutable
  data class Content(val languages: List<LanguageUiItem>, val isSaveButtonEnabled: Boolean) : LanguageUiState {
    @Immutable
    data class LanguageUiItem(
      val id: Long,
      val languageCode: Language.LanguageCode,
      val originalName: String,
      val isSelected: Boolean,
    )

    companion object {
      val initial
        get() = Content(
          languages = listOf(
            LanguageUiItem(
              id = 1,
              languageCode = Language.LanguageCode.En,
              originalName = "English",
              isSelected = true,
            ),
            LanguageUiItem(
              id = 2,
              languageCode = Language.LanguageCode.Ja,
              originalName = "Japanese",
              isSelected = false,
            ),
          ),
          isSaveButtonEnabled = false,
        )
    }
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
