package com.vmh.mvvmjetpackcompose.feature.search.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForInline
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchUiState
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent

@Composable
internal fun SearchResultContainer(
  searchResultUiState: SearchUiState.SearchResultUiState,
  lazyGridState: LazyGridState,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    when (searchResultUiState) {
      is SearchUiState.SearchResultUiState.Loading -> {
        // TODO: handle later
      }

      is SearchUiState.SearchResultUiState.Error ->
        CommonAppErrorContent(
          modifier = Modifier.fillMaxSize(),
          appError = searchResultUiState.appError,
          getAppErrorMessage = DefaultGetAppErrorMessageForInline,
          onRetry = onRetry,
        )

      is SearchUiState.SearchResultUiState.Content ->
        if (searchResultUiState.contents.isEmpty()) {
          SearchResultEmpty(keyword = searchResultUiState.keyword)
        } else {
          SearchResultsGrid(
            uiState = searchResultUiState,
            lazyGridState = lazyGridState,
          )
        }

      SearchUiState.SearchResultUiState.Empty -> Unit
    }
  }
}
