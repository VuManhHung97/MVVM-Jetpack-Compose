package com.vmh.mvvmjetpackcompose.feature.language.presentation.language

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("EmptyFunctionBlock", "UnusedParameter", "UnusedPrivateMember")
@HiltViewModel
internal class LanguageViewModel @Inject constructor() : ViewModel() {

  private val _uiStateFlow = MutableStateFlow(LanguageUiState.initial)

  val uiStateFlow: StateFlow<LanguageUiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (LanguageUiState) -> LanguageUiState) = _uiStateFlow.update(f)

  fun onLanguageSelect(languageId: String) {
  }

  fun onSaveClick() {
  }
}
