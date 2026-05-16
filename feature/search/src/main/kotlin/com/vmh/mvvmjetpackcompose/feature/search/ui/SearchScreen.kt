package com.vmh.mvvmjetpackcompose.feature.search.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.DebouncedClickable
import com.vmh.mvvmjetpackcompose.core.ui.common.LocalSnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarMessage
import com.vmh.mvvmjetpackcompose.core.ui.common.isScrolledToEnd
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.feature.search.ui.component.SearchKeywordTextField
import com.vmh.mvvmjetpackcompose.feature.search.ui.component.SearchResultContainer
import com.vmh.mvvmjetpackcompose.feature.search.ui.component.SearchSuggestionContainer
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import kotlinx.coroutines.launch

@Composable
internal fun SearchRoute(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SearchViewModel = hiltViewModel(),
  snackbarManager: SnackbarManager = LocalSnackbarManager.current,
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  val keyword by viewModel.keywordStateFlow.collectAsStateWithLifecycle(
    context = viewModel.viewModelScope.coroutineContext,
  )
  val scope = rememberCoroutineScope()
  val lazyGridState = rememberLazyGridState()
  val context = LocalContext.current

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      SearchSingleEvent.ScrollToTop ->
        scope.launch { lazyGridState.scrollToItem(index = 0) }

      is SearchSingleEvent.RemoveSearchHistoryFailure ->
        scope.launch {
          snackbarManager.show(
            snackbarMessage = SnackbarMessage.IconAndLabel(
              message = context.getString(CoreResourceR.string.app_error_some_thing_went_wrong),
              icon = SnackbarMessage.Icon(
                iconResId = CoreResourceR.drawable.ic_error_filled_24,
                tintColor = MVVMJetPackComposeColors.red40,
              ),
            ),
          )
        }
    }
  }

  SearchContent(
    modifier = modifier.fillMaxSize(),
    uiState = uiState,
    keyword = keyword,
    lazyGridState = lazyGridState,
    onNavigateBack = onNavigateBack,
    onSubmit = viewModel::onSubmit,
    onLoadMore = viewModel::onLoadMore,
    onKeywordClear = viewModel::onKeywordCleared,
    onKeywordChange = viewModel::onKeywordChanged,
    onSearchFieldFocusChange = { isFocused ->
      if (isFocused && uiState.suggestionUiState is SearchUiState.SuggestionUiState.Invisible) {
        viewModel.setSuggestionVisible(isVisible = true)
      }
    },
    onSuggestionItemClick = viewModel::onSuggestionItemClicked,
    onSuggestionItemRemove = viewModel::onSuggestionItemRemoved,
    onRetrySearchByKeyword = viewModel::retrySearchByKeyword,
  )
}

@Composable
private fun SearchContent(
  uiState: SearchUiState,
  keyword: String,
  lazyGridState: LazyGridState,
  onNavigateBack: () -> Unit,
  onSubmit: () -> Unit,
  onLoadMore: () -> Unit,
  onKeywordClear: () -> Unit,
  onRetrySearchByKeyword: () -> Unit,
  onSuggestionItemClick: (keyword: String) -> Unit,
  onSuggestionItemRemove: (keyword: String) -> Unit,
  onKeywordChange: (keyword: String) -> Unit,
  onSearchFieldFocusChange: (isFocused: Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val focusManager = LocalFocusManager.current
  var shouldRequestFocus by rememberSaveable { mutableStateOf(true) }
  val keyboardController = LocalSoftwareKeyboardController.current
  val searchKeywordFocusRequester = remember { FocusRequester() }
  var isSearchFieldFocused by remember { mutableStateOf(false) }
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)

  LaunchedEffect(lazyGridState) {
    snapshotFlow { lazyGridState.isScrolledToEnd(visibleThreshold = 2) }
      .collect { reachedBottom ->
        if (reachedBottom) {
          currentOnLoadMore()
        }
      }
  }

  DisposableEffect(shouldRequestFocus) {
    if (shouldRequestFocus) {
      searchKeywordFocusRequester.requestFocus()
      shouldRequestFocus = false
    }

    onDispose { }
  }

  DisposableEffect(lazyGridState.isScrollInProgress) {
    if (lazyGridState.isScrollInProgress) {
      keyboardController?.hide()
    }

    onDispose { }
  }

  Surface(
    modifier = modifier
      .statusBarsPadding()
      .fillMaxSize()
      .pointerInput(Unit) {
        detectTapGestures {
          focusManager.clearFocus()
        }
      },
  ) {
    Column {
      Row(
        modifier = Modifier
          .padding(vertical = 8.dp)
          .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = { DebouncedClickable.onClick(onNavigateBack) }) {
          Icon(
            modifier = Modifier.size(size = 20.dp),
            imageVector = ImageVector.vectorResource(CoreResourceR.drawable.ic_back),
            tint = MVVMJetPackComposeColors.Neutral10,
            contentDescription = null,
          )
        }

        SearchKeywordTextField(
          modifier = Modifier
            .weight(1f)
            .focusRequester(searchKeywordFocusRequester),
          value = keyword,
          onValueChange = onKeywordChange,
          onSubmit = {
            if (keyword.isNotBlank()) {
              focusManager.clearFocus()
              onSubmit()
            }
          },
          onReset = {
            onKeywordClear()
            searchKeywordFocusRequester.requestFocus()
          },
          onFocusChange = { isFocused ->
            isSearchFieldFocused = isFocused
            onSearchFieldFocusChange(isFocused)
          },
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
      ) {
        SearchResultContainer(
          searchResultUiState = uiState.searchResultUiState,
          lazyGridState = lazyGridState,
          onRetry = onRetrySearchByKeyword,
        )

        SearchSuggestionContainer(
          suggestionUiState = uiState.suggestionUiState,
          onSuggestionItemClick = {
            focusManager.clearFocus()
            onSuggestionItemClick(it)
          },
          onSuggestionItemRemove = onSuggestionItemRemove,
          onScrolling = { isScrolling ->
            if (isScrolling && isSearchFieldFocused) {
              focusManager.clearFocus()
            }
          },
        )
      }
    }
  }
}
