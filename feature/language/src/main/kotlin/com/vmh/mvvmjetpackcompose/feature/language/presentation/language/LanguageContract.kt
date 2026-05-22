package com.vmh.mvvmjetpackcompose.feature.language.presentation.language

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError

@Immutable
sealed interface LanguageUiState {
  companion object {
    val initial: LanguageUiState get() = Loading
  }

  @Immutable
  data object Loading : LanguageUiState

  @Immutable
  data class Content(val languages: List<LanguageItem>, val isSaveButtonEnabled: Boolean) : LanguageUiState {
    @Immutable
    data class LanguageItem(val id: String, val name: String, val localName: String, val isSelected: Boolean)

    companion object {
      val initial
        get() = Content(
          languages = listOf(
            LanguageItem(id = "en", name = "English", localName = "English", isSelected = true),
            LanguageItem(id = "es", name = "Español", localName = "Spanish", isSelected = false),
            LanguageItem(id = "ja", name = "日本語", localName = "Japanese", isSelected = false),
          ),
          isSaveButtonEnabled = false,
        )
    }
  }

  @Immutable
  data class Error(val error: AppError) : LanguageUiState
}
