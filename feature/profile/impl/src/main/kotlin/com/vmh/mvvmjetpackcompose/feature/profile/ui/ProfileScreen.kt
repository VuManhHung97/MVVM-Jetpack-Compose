package com.vmh.mvvmjetpackcompose.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForDialog
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForInline
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.profile.ui.ProfileUiState.ProfileUiItem
import com.vmh.mvvmjetpackcompose.feature.profile.ui.component.HeaderTitleContent
import com.vmh.mvvmjetpackcompose.feature.profile.ui.component.OptionContent
import com.vmh.mvvmjetpackcompose.feature.profile.ui.component.ProfileInfoContent
import com.vmh.mvvmjetpackcompose.feature.profile.ui.component.SignOutButton
import com.vmh.mvvmjetpackcompose.feature.profile.ui.component.SignOutDialog
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.WebViewDestination
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent

@Composable
internal fun ProfileRoute(
  onNavigateToAuthenticationScreen: () -> Unit,
  onNavigateToWebViewScreen: (destination: WebViewDestination) -> Unit,
  onNavigateToLanguageScreen: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ProfileViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  var logoutError by rememberSaveable { mutableStateOf<AppError?>(null) }
  val currentOnNavigateToAuthenticationScreen by rememberUpdatedState(onNavigateToAuthenticationScreen)
  var isProfileSignOutDialogVisible by rememberSaveable { mutableStateOf(false) }

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      is ProfileSingleEvent.LogoutFailure ->
        logoutError = event.error

      is ProfileSingleEvent.LogoutSuccess ->
        currentOnNavigateToAuthenticationScreen()
    }
  }

  Surface(
    modifier = modifier.fillMaxSize(),
    color = MVVMJetPackComposeColors.NeutralBlack,
  ) {
    Box(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.statusBars)
        .consumeWindowInsets(WindowInsets.statusBars)
        .fillMaxSize(),
    ) {
      when (val profilesContent = uiState.profilesContent) {
        is ProfileUiState.ProfilesContentUiState.Content ->
          ProfileContent(
            profilesContent = profilesContent,
            onItemClick = { item ->
              when (item) {
                ProfileUiItem.Option.FAQ ->
                  onNavigateToWebViewScreen(WebViewDestination.FAQ)

                ProfileUiItem.Option.PrivacyPolicy ->
                  onNavigateToWebViewScreen(WebViewDestination.PrivacyPolicy)

                ProfileUiItem.Option.TermsOfUse ->
                  onNavigateToWebViewScreen(WebViewDestination.TermsOfUse)

                ProfileUiItem.SignOut -> {
                  isProfileSignOutDialogVisible = true
                }

                ProfileUiItem.Option.Language ->
                  onNavigateToLanguageScreen()

                else -> {
                  // TODO: handle late
                }
              }
            },
          )

        is ProfileUiState.ProfilesContentUiState.Error -> {
          CommonAppErrorContent(
            appError = profilesContent.error,
            getAppErrorMessage = DefaultGetAppErrorMessageForInline,
            onDismiss = {},
            onConfirm = {},
          )
        }

        ProfileUiState.ProfilesContentUiState.Loading ->
          LoadingIndicator(
            modifier = Modifier.align(Alignment.Center),
          )
      }
    }

    if (isProfileSignOutDialogVisible) {
      SignOutDialog(
        onLogout = {
          isProfileSignOutDialogVisible = false
          viewModel.logout()
        },
        onDismiss = { isProfileSignOutDialogVisible = false },
      )
    }

    logoutError?.let {
      CommonAppErrorContent(
        appError = it,
        getAppErrorMessage = DefaultGetAppErrorMessageForDialog,
        onDismiss = { logoutError = null },
        onConfirm = { logoutError = null },
      )
    }
  }
}

@Composable
private fun ProfileContent(
  profilesContent: ProfileUiState.ProfilesContentUiState.Content,
  onItemClick: (item: ProfileUiItem) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(modifier = modifier) {
    itemsIndexed(
      items = profilesContent.items,
      key = { index, item -> item.getKey(index) },
      contentType = { _, item ->
        when (item) {
          is ProfileUiItem.Profile.Info -> "ProfileInfoContent"
          is ProfileUiItem.Profile.Empty -> "ProfileEmptyContent"
          is ProfileUiItem.Divider -> "HorizontalDivider"
          is ProfileUiItem.HeaderTitle -> "HeaderTitleContent"
          is ProfileUiItem.Option -> "OptionContent"
          is ProfileUiItem.SignOut -> "SignOutButton"
        }
      },
    ) { _, item ->
      when (item) {
        is ProfileUiItem.Profile.Info ->
          ProfileInfoContent(
            modifier = Modifier
              .clickable(onClick = { onItemClick(item) })
              .defaultSettingItemStyle()
              .padding(
                top = 16.dp,
                bottom = 8.dp,
              ),
            item = item,
          )

        ProfileUiItem.Profile.Empty -> Unit

        is ProfileUiItem.HeaderTitle ->
          HeaderTitleContent(
            modifier = Modifier
              .defaultSettingItemStyle()
              .padding(top = 12.dp, bottom = 4.dp),
            item = item,
          )

        is ProfileUiItem.Option ->
          OptionContent(
            modifier = Modifier
              .clickable(onClick = { onItemClick(item) })
              .defaultSettingItemStyle()
              .padding(vertical = 15.dp),
            item = item,
          )

        is ProfileUiItem.Divider ->
          HorizontalDivider(
            thickness = item.thickness,
            color = MVVMJetPackComposeColors.NeutralBlack,
          )

        ProfileUiItem.SignOut ->
          SignOutButton(
            modifier = Modifier
              .defaultSettingItemStyle()
              .padding(top = 24.dp, bottom = 12.dp),
            onClick = { onItemClick(item) },
          )
      }
    }
  }
}

private fun ProfileUiItem.getKey(index: Int) = when (this) {
  is ProfileUiItem.Profile.Info -> "profile_info"
  is ProfileUiItem.Profile.Empty -> "profile_empty"
  is ProfileUiItem.Divider -> "divider_$index"
  is ProfileUiItem.HeaderTitle -> this.textResId
  is ProfileUiItem.Option -> this.textResId
  is ProfileUiItem.SignOut -> "sign_out"
}

private fun Modifier.defaultSettingItemStyle(): Modifier = this
  .background(MVVMJetPackComposeColors.Neutral100)
  .fillMaxWidth()
  .padding(horizontal = 16.dp)

@Preview
@Composable
private fun SettingsRoutePreview() {
  MVVMJetpackComposeTheme {
    ProfileRoute(
      onNavigateToLanguageScreen = {},
      onNavigateToAuthenticationScreen = {},
      onNavigateToWebViewScreen = {},
    )
  }
}
