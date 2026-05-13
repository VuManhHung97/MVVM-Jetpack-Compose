package com.vmh.mvvmjetpackcompose.feature.search.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchUiState.SuggestionUiItem
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchUiState.SuggestionUiState
import com.vmh.mvvmjetpackcompose.feature.search.ui.SearchUiState.SuggestionUiType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Overlay component that displays search suggestions including history and autocomplete items.
 */
@Composable
internal fun SearchSuggestionOverlay(
  suggestionUiState: SuggestionUiState.Visible,
  onSuggestionItemClick: (keyword: String) -> Unit,
  onSuggestionItemRemove: (keyword: String) -> Unit,
  onIsScrollInProgressChange: (isScrolling: Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MVVMJetPackComposeColors.Neutral100),
  ) {
    when (suggestionUiState) {
      SuggestionUiState.Visible.Idle -> {
        // TODO(st-sangha): Implement idle state UI.
      }

      SuggestionUiState.Visible.Loading -> {
        // TODO(st-sangha): Implement loading state for suggestion overlay
      }

      is SuggestionUiState.Visible.Content -> SuggestionContent(
        modifier = Modifier.fillMaxWidth(),
        suggestions = suggestionUiState.suggestions,
        onSuggestionItemClick = onSuggestionItemClick,
        onSuggestionItemRemove = onSuggestionItemRemove,
        onScrolling = onIsScrollInProgressChange,
      )

      is SuggestionUiState.Visible.Error -> {
        // TODO(st-sangha): Implement error state for suggestion overlay
      }
    }
  }
}

private fun SuggestionUiType.getContentType(): String = when (this) {
  SuggestionUiType.History -> "HistorySuggestionItem"
  SuggestionUiType.Autocomplete -> "AutocompleteSuggestionItem"
}

@Composable
private fun SuggestionContent(
  suggestions: ImmutableList<SuggestionUiItem>,
  onSuggestionItemClick: (keyword: String) -> Unit,
  onSuggestionItemRemove: (keyword: String) -> Unit,
  onScrolling: (isScrolling: Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lazyListState = rememberLazyListState()
  val currentOnScrolling by rememberUpdatedState(onScrolling)

  LaunchedEffect(lazyListState) {
    snapshotFlow { lazyListState.isScrollInProgress }
      .collect { currentOnScrolling(it) }
  }

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    state = lazyListState,
  ) {
    items(
      items = suggestions,
      key = { it.id },
      contentType = { item -> item.type.getContentType() },
    ) { suggestion ->
      when (suggestion.type) {
        SuggestionUiType.History -> HistorySuggestionItem(
          suggestion = suggestion,
          onItemClick = { onSuggestionItemClick(suggestion.keyword) },
          onRemoveClick = { onSuggestionItemRemove(suggestion.keyword) },
        )

        SuggestionUiType.Autocomplete -> AutocompleteSuggestionItem(
          suggestion = suggestion,
          onItemClick = { onSuggestionItemClick(suggestion.keyword) },
        )
      }
    }
  }
}

@Composable
private fun HistorySuggestionItem(
  suggestion: SuggestionUiItem,
  onItemClick: () -> Unit,
  onRemoveClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .clickable(onClick = onItemClick)
        .padding(start = 24.dp, end = 60.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(
        modifier = Modifier.size(20.dp),
        imageVector = ImageVector.vectorResource(CoreResourceR.drawable.ic_baseline_recent_24),
        contentDescription = null,
        tint = MVVMJetPackComposeColors.Neutral10,
      )

      Text(
        modifier = Modifier.weight(1f),
        text = suggestion.keyword,
        style = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
        color = MVVMJetPackComposeColors.NeutralWhite,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }

    IconButton(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 12.dp),
      onClick = onRemoveClick,
    ) {
      Icon(
        modifier = Modifier.size(20.dp),
        imageVector = ImageVector.vectorResource(CoreResourceR.drawable.ic_closed),
        contentDescription = null,
        tint = MVVMJetPackComposeColors.Neutral10,
      )
    }
  }
}

@Composable
private fun AutocompleteSuggestionItem(
  suggestion: SuggestionUiItem,
  onItemClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .clickable(onClick = onItemClick)
      .padding(horizontal = 24.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Icon(
      modifier = Modifier.size(20.dp),
      imageVector = ImageVector.vectorResource(CoreResourceR.drawable.ic_search),
      contentDescription = null,
      tint = MVVMJetPackComposeColors.Neutral10,
    )

    Text(
      modifier = Modifier.weight(1f),
      text = suggestion.keyword,
      style = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
      color = MVVMJetPackComposeColors.Neutral10,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Preview
@Composable
private fun SearchSuggestionOverlayPreview() {
  MVVMJetpackComposeTheme {
    Surface {
      SearchSuggestionOverlay(
        suggestionUiState = SuggestionUiState.Visible.Content(
          suggestions = persistentListOf(
            SuggestionUiItem(
              keyword = "The new look and the instigators, the gorge",
              type = SuggestionUiType.History,
            ),
            SuggestionUiItem(
              keyword = "The Gorge",
              type = SuggestionUiType.History,
            ),
            SuggestionUiItem(
              keyword = "The",
              type = SuggestionUiType.Autocomplete,
            ),
            SuggestionUiItem(
              keyword = "The Instigators",
              type = SuggestionUiType.Autocomplete,
            ),
            SuggestionUiItem(
              keyword = "The New Look",
              type = SuggestionUiType.Autocomplete,
            ),
          ),
        ),
        onSuggestionItemClick = {},
        onSuggestionItemRemove = {},
        onIsScrollInProgressChange = {},
      )
    }
  }
}
