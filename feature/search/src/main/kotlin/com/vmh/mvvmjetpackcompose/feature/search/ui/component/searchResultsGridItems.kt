package com.vmh.mvvmjetpackcompose.feature.search.ui.component

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchUiState
import kotlinx.collections.immutable.ImmutableList

internal fun LazyGridScope.searchResultsGridItems(
  searchResultItems: ImmutableList<SearchUiState.ResultContentUiItem>,
) {
  items(
    items = searchResultItems,
    key = { it.id },
    contentType = { "SearchResultItem" },
    span = { GridItemSpan(currentLineSpan = maxLineSpan) },
  ) { item ->
    Text(
      text = item.title,
      textAlign = TextAlign.Center,
      style = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
      color = MVVMJetPackComposeColors.Neutral20,
    )
  }
}
