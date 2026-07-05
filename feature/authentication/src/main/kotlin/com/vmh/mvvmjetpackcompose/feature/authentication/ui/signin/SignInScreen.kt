// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.analytics.TrackScreenViewEvent
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForDialog
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminSerif
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.SignInSingleEvent
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.SignInUiState
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.SignInViewModel
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.AdminTextField
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GoldButton
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GoldDivider
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.LogoBadge

private const val SignInScreenName = "SignIn"

// The Võ Lâm 2 admin login intentionally has no back button / sign-up link, so those flow callbacks
// are accepted (to keep the authentication graph wiring unchanged) but not rendered.
@Suppress("UnusedParameter")
@Composable
internal fun SignInRoute(
  onNavigateBack: () -> Unit,
  onNavigateToSignUpScreen: () -> Unit,
  navigateToAuthenticationScreen: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SignInViewModel = hiltViewModel(),
) {
  TrackScreenViewEvent(screenName = SignInScreenName)

  val focusManager = LocalFocusManager.current
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  var appErrorToDisplay by rememberSaveable { mutableStateOf<AppError?>(null) }
  val currentNavigateToAuthenticationScreen by rememberUpdatedState(navigateToAuthenticationScreen)

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      is SignInSingleEvent.SignInFailure -> appErrorToDisplay = event.error
      SignInSingleEvent.SignInSuccess -> currentNavigateToAuthenticationScreen()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
  ) {
    SignInContent(
      uiState = uiState,
      onEmailChange = viewModel::emailChanged,
      onPasswordChange = viewModel::passwordChanged,
      onSubmit = viewModel::signIn,
    )

    if (uiState.isLoading) {
      LoadingIndicator(modifier = Modifier.align(Alignment.Center))
    }

    appErrorToDisplay?.let {
      CommonAppErrorContent(
        appError = it,
        getAppErrorMessage = DefaultGetAppErrorMessageForDialog,
        onDismiss = { appErrorToDisplay = null },
        onConfirm = { appErrorToDisplay = null },
      )
    }
  }
}

@Composable
private fun SignInContent(
  uiState: SignInUiState,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onSubmit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isSubmitEnabled = uiState.email.isNotBlank() && uiState.password.isNotEmpty()
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(0f to MVVMJetPackComposeColors.PageTop, 1f to MVVMJetPackComposeColors.PageMid),
      )
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 28.dp, vertical = 32.dp),
    verticalArrangement = Arrangement.Center,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      LogoBadge(size = 64, fontSize = 24, radius = 16)
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = stringResource(R.string.sign_in_admin_app_name),
          color = MVVMJetPackComposeColors.InkTitle,
          fontSize = 26.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = GameAdminSerif,
        )
        Text(
          text = stringResource(R.string.sign_in_admin_subtitle),
          color = MVVMJetPackComposeColors.Muted,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 4.dp),
        )
      }
      GoldDivider(modifier = Modifier.width(120.dp).height(1.dp))
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      FieldLabel(text = stringResource(R.string.sign_in_admin_account_label))
      AdminTextField(
        value = uiState.email,
        onValueChange = onEmailChange,
        placeholder = stringResource(R.string.sign_in_admin_account_placeholder),
      )
      FieldLabel(text = stringResource(R.string.sign_in_admin_password_label))
      AdminTextField(
        value = uiState.password,
        onValueChange = onPasswordChange,
        placeholder = stringResource(R.string.sign_in_admin_password_placeholder),
        isPassword = true,
      )
      GoldButton(
        text = stringResource(R.string.sign_in_admin_submit),
        onClick = onSubmit,
        enabled = isSubmitEnabled,
        fontSize = 15,
        minHeight = 50.dp,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
      )
      Text(
        text = stringResource(R.string.sign_in_admin_footer),
        color = MVVMJetPackComposeColors.Faint,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
      )
    }
  }
}

@Composable
private fun FieldLabel(text: String, modifier: Modifier = Modifier) {
  Text(
    modifier = modifier,
    text = text,
    color = MVVMJetPackComposeColors.Muted,
    fontSize = 11.sp,
    fontWeight = FontWeight.Bold,
  )
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun SignInContentPreview() {
  MVVMJetpackComposeTheme {
    SignInContent(
      uiState = SignInUiState.initial,
      onEmailChange = {},
      onPasswordChange = {},
      onSubmit = {},
    )
  }
}
