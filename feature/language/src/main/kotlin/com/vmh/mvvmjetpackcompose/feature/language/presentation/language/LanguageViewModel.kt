package com.vmh.mvvmjetpackcompose.feature.language.presentation.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.getOrElse
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
      val languages = languageRepository.getLanguages()
        .getOrElse { error ->
          Timber.e(error, "GetLanguages failure")
          emitState { LanguageUiState.Error(error = error) }
          return@launch
        }

      languageRepository.observeCurrentLocale()
        .map { it.getOrElse { null }?.toLanguageTag() ?: LanguageRepository.DEFAULT_LOCALE.toLanguageTag() }
        .distinctUntilChanged()
        .collect { localeTag ->
          val selectedId = languages.find { it.languageCode.tag == localeTag }?.id
          emitState {
            LanguageUiState.Content(
              languages = languages.mapToPersistentList { language ->
                language.toLanguageUiItem(isSelected = language.id == selectedId)
              },
              isSaveButtonEnabled = false,
            )
          }
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
          isSaveButtonEnabled = true,
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
