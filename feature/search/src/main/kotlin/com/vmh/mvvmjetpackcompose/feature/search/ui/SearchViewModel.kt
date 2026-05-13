package com.vmh.mvvmjetpackcompose.feature.search.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("UnusedParameter", "UnusedPrivateMember", "TooManyFunctions", "EmptyFunctionBlock")
@HiltViewModel
internal class SearchViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val eventChannel: EventChannel<SearchSingleEvent>,
) : ViewModel(eventChannel),
  HasEventFlow<SearchSingleEvent> by eventChannel {
  private val _uiStateFlow: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState.Initial)
  val uiStateFlow: StateFlow<SearchUiState> = _uiStateFlow.asStateFlow()
  val keywordStateFlow: StateFlow<String> = savedStateHandle.getStateFlow(KEYWORD_KEY, "")
  private val submitFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
  private inline fun emitState(f: (SearchUiState) -> SearchUiState) = _uiStateFlow.update(f)

  init {
    observeSuggestionKeywordChange()
    observeSearchSubmit()
  }

  private fun observeSearchSubmit() {
    viewModelScope.launch {
      submitFlow
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .collectLatest(::searchByKeyword)
    }
  }

  private fun observeSuggestionKeywordChange() {
    viewModelScope.launch {
      uiStateFlow
        .map { it.suggestionUiState is SearchUiState.SuggestionUiState.Visible }
        .distinctUntilChanged()
        .flatMapLatest { isSuggestionVisible ->
          if (isSuggestionVisible) {
            keywordStateFlow
              .debounce(SEARCH_DEBOUNCE_DURATION)
              .map { it.trim() }
              .distinctUntilChanged()
          } else {
            flowOf(null)
          }
        }
        .collectLatest(::getSuggestions)
    }
  }

  fun retrySearchByKeyword() {
    if (uiStateFlow.value.searchResultUiState is SearchUiState.SearchResultUiState.Error) {
      viewModelScope.launch { submitFlow.emit(keywordStateFlow.value) }
    }
  }

  private suspend fun getSuggestions(keyword: String?) {
  }

  private suspend fun searchByKeyword(keyword: String) {
  }

  fun onSubmit() {
    setSuggestionVisible(isVisible = false)
    viewModelScope.launch { submitFlow.emit(keywordStateFlow.value) }
  }

  fun onLoadMore() {
  }

  fun onSuggestionItemClicked(keyword: String) {
    setSuggestionVisible(isVisible = false)
    onKeywordChanged(keyword)
    onSubmit()
  }

  fun setSuggestionVisible(isVisible: Boolean) {
    emitState { state ->
      state.copy(
        suggestionUiState = if (isVisible) {
          SearchUiState.SuggestionUiState.Visible.Idle
        } else {
          SearchUiState.SuggestionUiState.Invisible
        },
      )
    }
  }

  fun onSuggestionItemRemoved(keyword: String) {
    viewModelScope.launch {
    }
  }

  fun onKeywordCleared() {
    setSuggestionVisible(isVisible = true)
    savedStateHandle[KEYWORD_KEY] = ""
  }

  fun onKeywordChanged(keyword: String) {
    savedStateHandle[KEYWORD_KEY] = keyword
  }

  private companion object {
    const val KEYWORD_KEY = "SearchViewModel#keyword"
    const val SEARCH_LIMIT = 12
    const val SEARCH_SUGGESTIONS_LIMIT = 10
    val SEARCH_DEBOUNCE_DURATION = 500.milliseconds

    private fun determineLoadMoreState(nextPageSize: Int): SearchUiState.LoadMoreState = if (nextPageSize <
      SEARCH_LIMIT
    ) {
      SearchUiState.LoadMoreState.EndOfList
    } else {
      SearchUiState.LoadMoreState.None
    }
  }
}
