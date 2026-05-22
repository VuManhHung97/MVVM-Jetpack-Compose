package com.vmh.mvvmjetpackcompose.feature.language.ui.language

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.LanguageUiState
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.LanguageViewModel
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton

@Composable
internal fun LanguageRoute(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LanguageViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

  LanguageScreen(
    uiState = uiState,
    onNavigateBack = onNavigateBack,
    modifier = modifier,
  )
}

@Suppress("UnusedParameter")
@Composable
internal fun LanguageScreen(uiState: LanguageUiState, onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
  Scaffold(
    modifier = modifier,
    topBar = {
      BackIconButton(
        modifier = Modifier.padding(start = 6.dp, top = 40.dp),
        onBackClick = onNavigateBack,
      )
    },
    content = { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues = innerPadding)
          .consumeWindowInsets(paddingValues = innerPadding),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = "Language Screen")
      }
    },
  )
}

@Preview(showBackground = true)
@Composable
private fun LanguageScreenPreview() {
  MVVMJetpackComposeTheme {
    LanguageScreen(
      uiState = LanguageUiState.initial,
      onNavigateBack = {},
    )
  }
}
