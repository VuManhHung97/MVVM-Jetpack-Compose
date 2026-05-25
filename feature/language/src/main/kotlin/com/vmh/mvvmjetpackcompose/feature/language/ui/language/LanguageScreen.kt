package com.vmh.mvvmjetpackcompose.feature.language.ui.language

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.model.language.Language
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForInline
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.LanguageSingleEvent
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.LanguageUiState
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.LanguageViewModel
import com.vmh.mvvmjetpackcompose.feature.language.ui.language.component.LanguageItem
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent

@Composable
internal fun LanguageRoute(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LanguageViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      is LanguageSingleEvent.LanguageChangeFailure -> Unit
      LanguageSingleEvent.LanguageChangeSuccess -> Unit
    }
  }

  LanguageContent(
    modifier = modifier,
    uiState = uiState,
    onNavigateBack = onNavigateBack,
    onLanguageItemSelect = viewModel::selectLanguage,
    onLanguageChange = viewModel::languageChanges,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageContent(
  uiState: LanguageUiState,
  onNavigateBack: () -> Unit,
  onLanguageItemSelect: (languageId: Long) -> Unit,
  onLanguageChange: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(id = CoreResourceR.string.language_title),
            style = MVVMJetpackComposeTheme.typography.textStyleXLargeBold,
          )
        },
        navigationIcon = {
          BackIconButton(onBackClick = onNavigateBack)
        },
        actions = {
          TextButton(
            onClick = onLanguageChange,
            enabled = uiState is LanguageUiState.Content && uiState.isSaveButtonEnabled,
          ) {
            Text(
              text = stringResource(id = CoreResourceR.string.language_save),
              style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
              color = MVVMJetPackComposeColors.NeutralWhite,
            )
          }
        },
      )
    },
    content = { innerPadding ->
      when (uiState) {
        LanguageUiState.Loading -> {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues = innerPadding)
              .consumeWindowInsets(paddingValues = innerPadding),
            contentAlignment = Alignment.Center,
          ) {
            LoadingIndicator()
          }
        }
        is LanguageUiState.Content -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues = innerPadding)
              .consumeWindowInsets(paddingValues = innerPadding),
          ) {
            items(
              items = uiState.languages,
              key = { language -> language.id },
              contentType = { "LanguageItem" },
            ) { language ->
              val languageName = when (language.languageCode) {
                Language.LanguageCode.En ->
                  stringResource(CoreResourceR.string.language_english)

                Language.LanguageCode.Ja ->
                  stringResource(CoreResourceR.string.language_japanese)

                Language.LanguageCode.Unknown -> return@items
              }

              LanguageItem(
                name = languageName,
                localName = language.originalName,
                isSelected = language.isSelected,
                onLanguageItemClick = { onLanguageItemSelect(language.id) },
              )
            }
          }
        }
        is LanguageUiState.Error -> {
          CommonAppErrorContent(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues = innerPadding)
              .consumeWindowInsets(paddingValues = innerPadding),
            appError = uiState.error,
            getAppErrorMessage = DefaultGetAppErrorMessageForInline,
          )
        }
      }
    },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun LanguageContentPreview() {
  MVVMJetpackComposeTheme {
    LanguageContent(
      uiState = LanguageUiState.Content.initial,
      onNavigateBack = {},
      onLanguageItemSelect = {},
      onLanguageChange = {},
    )
  }
}
