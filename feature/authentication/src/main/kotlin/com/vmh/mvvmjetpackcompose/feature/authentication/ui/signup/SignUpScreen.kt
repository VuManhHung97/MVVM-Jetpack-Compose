package com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.DebouncedClickable
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.SignUpUiState
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.SignUpViewModel
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.component.ConfirmPasswordTextField
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.component.EmailTextField
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.component.PasswordTextField
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.component.SignUpButton
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton

@Composable
internal fun SignUpScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SignUpViewModel = hiltViewModel(),
) {
  val focusManager = LocalFocusManager.current
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

  Scaffold(
    modifier = modifier.pointerInput(Unit) {
      detectTapGestures {
        focusManager.clearFocus() // Clear focus when tapped outside
      }
    },
    containerColor = MVVMJetPackComposeColors.Neutral100,
    topBar = {
      BackIconButton(
        modifier = Modifier.padding(start = 6.dp, top = 40.dp),
        onBackClick = { DebouncedClickable.onClick(onNavigateBack) },
      )
    },
    content = { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues = innerPadding)
          .consumeWindowInsets(paddingValues = innerPadding),
      ) {
        SignUpContent(
          modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 24.dp)
            .fillMaxSize(),
          uiState = uiState,
          onEmailChange = viewModel::onEmailChange,
          onPasswordChange = viewModel::onPasswordChange,
          onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
          onSignUpValidate = viewModel::signUp,
        )

        if (uiState.isLoading) {
          LoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
      }
    },
  )
}

@Composable
private fun SignUpContent(
  uiState: SignUpUiState,
  onEmailChange: (value: String) -> Unit,
  onPasswordChange: (value: String) -> Unit,
  onConfirmPasswordChange: (value: String) -> Unit,
  onSignUpValidate: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var isPasswordVisible by rememberSaveable { mutableStateOf(false) } // Default: password hidden
  var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) } // Default: confirm password hidden

  Column(modifier = modifier) {
    Text(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .align(Alignment.CenterHorizontally),
      text = stringResource(CoreResourceR.string.sign_up_title),
      style = MVVMJetpackComposeTheme.typography.textStyleXXXXLargeBold,
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )

    Text(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      text = stringResource(CoreResourceR.string.sign_up_content),
      textAlign = TextAlign.Center,
      color = MVVMJetPackComposeColors.Neutral20,
      style = MVVMJetpackComposeTheme.typography.textStyleBaseRegular,
    )

    Text(
      modifier = Modifier.padding(top = 24.dp),
      text = stringResource(CoreResourceR.string.sign_up_email),
      style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )

    EmailTextField(
      modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth(),
      uiState = uiState,
      onEmailChange = onEmailChange,
    )

    Text(
      modifier = Modifier.padding(top = 16.dp),
      text = stringResource(CoreResourceR.string.sign_up_password),
      style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )

    PasswordTextField(
      modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth(),
      uiState = uiState,
      isPasswordVisible = isPasswordVisible,
      onPasswordChange = onPasswordChange,
      onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
    )

    Text(
      modifier = Modifier.padding(top = 16.dp),
      text = stringResource(CoreResourceR.string.sign_up_confirm_password),
      style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )

    ConfirmPasswordTextField(
      modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth(),
      uiState = uiState,
      isConfirmPasswordVisible = isConfirmPasswordVisible,
      onConfirmPasswordChange = onConfirmPasswordChange,
      onConfirmPasswordVisibilityToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
    )

    SignUpButton(
      modifier = Modifier
        .padding(top = 24.dp)
        .fillMaxWidth()
        .height(40.dp),
      onClick = onSignUpValidate,
    )
  }
}
