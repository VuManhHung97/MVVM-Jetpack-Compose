package com.vmh.mvvmjetpackcompose.feature.search.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchUiState

private const val SearchGridColumnCount = 6

@Composable
internal fun SearchResultsGrid(uiState: SearchUiState.SearchResultUiState.Content, lazyGridState: LazyGridState) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(count = SearchGridColumnCount),
    contentPadding = PaddingValues(10.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    state = lazyGridState,
  ) {
    searchResultsGridItems(
      searchResultItems = uiState.contents,
    )

    when (uiState.loadMoreState) {
      SearchUiState.LoadMoreState.None -> Unit

      SearchUiState.LoadMoreState.Loading ->
        item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(all = 16.dp),
          ) {
            LoadingIndicator(modifier = Modifier.align(alignment = Alignment.Center))
          }
        }

      is SearchUiState.LoadMoreState.Error -> {
        // TODO: Show error message for loading more items
      }

      SearchUiState.LoadMoreState.EndOfList -> {
        // No more items to load
        // Could show the view for end of list if needed
      }
    }
  }
}
