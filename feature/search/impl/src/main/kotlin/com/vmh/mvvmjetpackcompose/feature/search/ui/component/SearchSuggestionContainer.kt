package com.vmh.mvvmjetpackcompose.feature.search.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchUiState

@Composable
internal fun SearchSuggestionContainer(
  suggestionUiState: SearchUiState.SuggestionUiState,
  onScrolling: (isScrolling: Boolean) -> Unit,
  onSuggestionItemClick: (keyword: String) -> Unit,
  onSuggestionItemRemove: (keyword: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    when (suggestionUiState) {
      SearchUiState.SuggestionUiState.Invisible -> Unit

      is SearchUiState.SuggestionUiState.Visible -> SearchSuggestionOverlay(
        suggestionUiState = suggestionUiState,
        onSuggestionItemClick = onSuggestionItemClick,
        onSuggestionItemRemove = onSuggestionItemRemove,
        onIsScrollInProgressChange = onScrolling,
      )
    }
  }
}
