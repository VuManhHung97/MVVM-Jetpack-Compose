package com.vmh.mvvmjetpackcompose.feature.search.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.onFailure
import com.vmh.mvvmjetpackcompose.core.common.extension.buildPersistentList
import com.vmh.mvvmjetpackcompose.core.common.extension.filterDuplicatesAndAddAll
import com.vmh.mvvmjetpackcompose.core.common.extension.mapToPersistentList
import com.vmh.mvvmjetpackcompose.core.domain.repository.SearchRepository
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Suppress("TooManyFunctions", "EmptyFunctionBlock", "UnusedParameter")
@HiltViewModel
internal class SearchViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val eventChannel: EventChannel<SearchSingleEvent>,
  private val searchRepository: SearchRepository,
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
    if (keyword == null) return

    emitState { it.copy(suggestionUiState = SearchUiState.SuggestionUiState.Visible.Loading) }

    coroutineScope {
      val historyDeferred = async {
        searchRepository
          .getSearchHistory(
            keyword = keyword,
            limit = SEARCH_SEARCH_HISTORY_LIMIT,
          )
          .onFailure {
            Timber.e(it, "Failed to fetch search history for keyword: $keyword")
          }
          .getOrElse { emptyList() }
      }
      val autocompleteDeferred = async {
        searchRepository
          .getSearchAutocomplete(
            keyword = keyword,
            limit = SEARCH_SEARCH_SUGGESTIONS_LIMIT,
          )
          .onFailure {
            Timber.e(it, "Failed to fetch search autocomplete for keyword: $keyword")
          }
          .getOrElse { emptyList() }
      }
      val (historyTexts, autocompleteTexts) = awaitAll(historyDeferred, autocompleteDeferred)

      val items = buildPersistentList {
        addAll(historyTexts.map { it.toHistorySuggestionUiItem() })
        addAll(autocompleteTexts.map { it.toAutocompleteSuggestionUiItem() })
      }
      emitState { it.copy(suggestionUiState = SearchUiState.SuggestionUiState.Visible.Content(items)) }
    }
  }

  private suspend fun searchByKeyword(keyword: String) {
    emitState { it.copy(searchResultUiState = SearchUiState.SearchResultUiState.Loading) }

    searchRepository
      .searchByKeyword(
        keyword = keyword,
        limit = SEARCH_LIMIT,
        offset = 0,
      )
      .fold(
        success = { items ->
          emitState { state ->
            state.copy(
              searchResultUiState = SearchUiState.SearchResultUiState.Content(
                keyword = keyword,
                contents = items.mapToPersistentList { it.toSearchResultContentUiItem() },
                loadMoreState = determineLoadMoreState(items.size),
              ),
            )
          }
          eventChannel.send(SearchSingleEvent.ScrollToTop)
        },
        failure = { error ->
          Timber.e(error, "Failed to search for keyword: $keyword")
          emitState { it.copy(searchResultUiState = SearchUiState.SearchResultUiState.Error(error)) }
        },
      )
  }

  fun onSubmit() {
    setSuggestionVisible(isVisible = false)
    viewModelScope.launch { submitFlow.emit(keywordStateFlow.value) }
  }

  fun onLoadMore() {
    val currentContent = uiStateFlow.value.searchResultUiState
    if (currentContent !is SearchUiState.SearchResultUiState.Content) return

    when (currentContent.loadMoreState) {
      SearchUiState.LoadMoreState.None,
      is SearchUiState.LoadMoreState.Error,
      -> Unit

      SearchUiState.LoadMoreState.Loading,
      SearchUiState.LoadMoreState.EndOfList,
      -> return
    }

    val keyword = currentContent.keyword
    val offset = currentContent.contents.size

    viewModelScope.launch {
      emitState { state ->
        state.updateSearchResultContent { existingContent ->
          existingContent.copy(loadMoreState = SearchUiState.LoadMoreState.Loading)
        }
      }

      searchRepository
        .searchByKeyword(
          keyword = keyword,
          limit = SEARCH_LIMIT,
          offset = offset,
        )
        .fold(
          success = { items ->
            emitState { state ->
              state.updateSearchResultContent { existingContent ->
                existingContent.copy(
                  contents = existingContent.contents.filterDuplicatesAndAddAll(
                    items = items.mapToPersistentList { it.toSearchResultContentUiItem() },
                    keySelector = { it.id },
                  ),
                  loadMoreState = determineLoadMoreState(items.size),
                )
              }
            }
          },
          failure = { error ->
            Timber.e(error, "Failed to load more for keyword: $keyword, offset: $offset")
            emitState { state ->
              state.updateSearchResultContent { existingContent ->
                existingContent.copy(loadMoreState = SearchUiState.LoadMoreState.Error(error))
              }
            }
          },
        )
    }
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
    const val SEARCH_SEARCH_SUGGESTIONS_LIMIT = 10
    const val SEARCH_SEARCH_HISTORY_LIMIT = 10
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

private inline fun SearchUiState.updateSearchResultContent(
  transform: (SearchUiState.SearchResultUiState.Content) -> SearchUiState.SearchResultUiState.Content,
): SearchUiState = when (val currentResult = searchResultUiState) {
  is SearchUiState.SearchResultUiState.Content -> copy(
    searchResultUiState = transform(currentResult),
  )

  is SearchUiState.SearchResultUiState.Empty,
  is SearchUiState.SearchResultUiState.Loading,
  is SearchUiState.SearchResultUiState.Error,
  -> this
}
