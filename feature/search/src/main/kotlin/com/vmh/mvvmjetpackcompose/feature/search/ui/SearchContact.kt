package com.vmh.mvvmjetpackcompose.feature.search.ui

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList

data class SearchUiState(val searchResultUiState: SearchResultUiState, val suggestionUiState: SuggestionUiState) {
  companion object {
    val Initial
      get() = SearchUiState(
        searchResultUiState = SearchResultUiState.Empty,
        suggestionUiState = SuggestionUiState.Visible.Idle,
      )
  }

  sealed interface SearchResultUiState {
    @Immutable
    data object Empty : SearchResultUiState

    @Immutable
    data object Loading : SearchResultUiState

    @Immutable
    data class Content(
      val keyword: String,
      val contents: PersistentList<ResultContentUiItem>,
      val loadMoreState: LoadMoreState,
    ) : SearchResultUiState

    @Immutable
    data class Error(val appError: AppError) : SearchResultUiState
  }

  @Immutable
  sealed interface SuggestionUiState {
    @Immutable
    data object Invisible : SuggestionUiState

    @Immutable
    sealed interface Visible : SuggestionUiState {
      @Immutable
      data object Idle : Visible

      @Immutable
      data object Loading : Visible

      @Immutable
      data class Content(val suggestions: ImmutableList<SuggestionUiItem>) : Visible

      @Immutable
      data class Error(val appError: AppError) : Visible
    }
  }

  @Immutable
  data class SuggestionUiItem(val keyword: String, val type: SuggestionUiType) {
    val id = "$keyword-$type"
  }

  @Immutable
  enum class SuggestionUiType {
    History,
    Autocomplete,
  }

  @Immutable
  sealed interface LoadMoreState {
    @Immutable
    data object None : LoadMoreState

    @Immutable
    data object Loading : LoadMoreState

    @Immutable
    data class Error(val appError: AppError) : LoadMoreState

    @Immutable
    data object EndOfList : LoadMoreState
  }

  @Immutable
  data class ResultContentUiItem(val id: Long, val title: String)
}

@Immutable
sealed interface SearchSingleEvent {
  @Immutable
  data object ScrollToTop : SearchSingleEvent

  @Immutable
  data class RemoveSearchHistoryFailure(val error: AppError) : SearchSingleEvent
}
