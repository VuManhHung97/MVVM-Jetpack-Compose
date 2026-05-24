package com.vmh.mvvmjetpackcompose.feature.language.presentation.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.coroutines.parZip
import com.github.michaelbull.result.fold
import com.vmh.mvvmjetpackcompose.core.common.extension.mapToPersistentList
import com.vmh.mvvmjetpackcompose.core.domain.repository.LanguageRepository
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
internal class LanguageViewModel @Inject constructor(
  private val languageRepository: LanguageRepository,
  private val eventChannel: EventChannel<LanguageSingleEvent>,
) : ViewModel(eventChannel),
  HasEventFlow<LanguageSingleEvent> by eventChannel {

  private val _uiStateFlow = MutableStateFlow(LanguageUiState.initial)

  val uiStateFlow: StateFlow<LanguageUiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (LanguageUiState) -> LanguageUiState) = _uiStateFlow.update(f)

  init {
    getLanguages()
  }

  private fun getLanguages() {
    emitState { LanguageUiState.Loading }

    viewModelScope.launch {
      val result = parZip(
        { languageRepository.getLanguages() },
        { languageRepository.observeCurrentLocale().first() },
      ) { languages, currentLocale ->
        val storedLocaleTag = (currentLocale ?: LanguageRepository.DEFAULT_LOCALE).toLanguageTag()
        val selectedId = languages.find { it.languageCode.tag == storedLocaleTag }?.id
        val items = languages.mapToPersistentList { language ->
          val isSelected = language.id == selectedId
          language.toLanguageUiItem(isSelected = isSelected)
        }

        LanguageUiState.Content(
          languages = items,
          isSaveButtonEnabled = items.any { it.isSelected },
        )
      }

      emitState {
        result.fold(
          success = { it },
          failure = { error ->
            Timber.e(error, "GetLanguages failure")
            LanguageUiState.Error(error = error)
          },
        )
      }
    }
  }

  internal fun selectLanguage(languageId: Long) {
    emitState { state ->
      if (state is LanguageUiState.Content) {
        val updateLanguages = state
          .languages
          .mapToPersistentList { item ->
            item.copy(isSelected = item.id == languageId)
          }
        state.copy(
          languages = updateLanguages,
          isSaveButtonEnabled = updateLanguages.any { it.id == languageId },
        )
      } else {
        state
      }
    }
  }

  internal fun languageChanges() {
    val currentState = _uiStateFlow.value
    if (currentState !is LanguageUiState.Content) return
    val selectLanguageTag = currentState.languages.firstOrNull { it.isSelected }?.languageCode?.tag ?: return
    val locale = Locale.forLanguageTag(selectLanguageTag)

    viewModelScope.launch {
      languageRepository.setCurrentLocale(locale = locale)
        .fold(
          success = { eventChannel.send(LanguageSingleEvent.LanguageChangeSuccess) },
          failure = { error ->
            eventChannel.send(LanguageSingleEvent.LanguageChangeFailure(error))
            Timber.e(error, "language changes failure")
          },
        )
    }
  }
}
