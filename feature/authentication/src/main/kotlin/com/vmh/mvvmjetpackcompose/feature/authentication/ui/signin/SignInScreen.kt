package com.vmh.mvvmjetpackcompose.feature.authentication.ui.signin

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.DebouncedClickable
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.SignInUiState
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.SignInViewModel
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.ValidationStatus
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton
import com.vmh.mvvmjetpackcompose.ui.widget.common.CustomizedOutlinedTextField

private val TextFieldShape = RoundedCornerShape(12.dp)

@Composable
internal fun SignInRoute(
  onNavigateBack: () -> Unit,
  onNavigateToSignUpScreen: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SignInViewModel = hiltViewModel(),
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
        SignInContent(
          modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 24.dp),
          uiState = uiState,
          onEmailChange = viewModel::emailChanged,
          onPasswordChange = viewModel::passwordChanged,
          onSignInValidate = viewModel::signIn,
          onNavigateToSignUpScreen = onNavigateToSignUpScreen,
          onNavigateToForgotPasswordScreen = {},
        )

        if (uiState.isLoading) {
          LoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
      }
    },
  )
}

@Composable
private fun signInContentTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
  focusedTextColor = MVVMJetPackComposeColors.Neutral10,
  focusedBorderColor = MVVMJetPackComposeColors.Neutral40,
  unfocusedTextColor = MVVMJetPackComposeColors.Neutral10,
  unfocusedBorderColor = MVVMJetPackComposeColors.Neutral70,
  cursorColor = MVVMJetPackComposeColors.NeutralWhite,
  errorCursorColor = MVVMJetPackComposeColors.NeutralWhite,
  focusedPlaceholderColor = MVVMJetPackComposeColors.Neutral40,
  unfocusedPlaceholderColor = MVVMJetPackComposeColors.Neutral40,
  errorTextColor = MVVMJetPackComposeColors.Neutral10,
  errorBorderColor = MVVMJetPackComposeColors.red40,
  errorTrailingIconColor = MVVMJetPackComposeColors.Neutral10,
)

@Suppress("LongMethod")
@Composable
private fun SignInContent(
  uiState: SignInUiState,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onSignInValidate: () -> Unit,
  onNavigateToSignUpScreen: () -> Unit,
  onNavigateToForgotPasswordScreen: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val textFieldColors = signInContentTextFieldColors()
  var isPasswordVisible by rememberSaveable { mutableStateOf(false) } // Default: password hidden

  val emailValidationStatus = uiState.emailValidationStatus
  val passwordValidationStatus = uiState.passwordValidationStatus

  Column(modifier = modifier) {
    Text(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .align(Alignment.CenterHorizontally),
      text = stringResource(CoreResourceR.string.sign_in_title),
      style = MVVMJetpackComposeTheme.typography.textStyleXXXXLargeBold,
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )

    Text(
      modifier = Modifier.padding(top = 24.dp),
      text = stringResource(CoreResourceR.string.sign_up_email),
      style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )

    CustomizedOutlinedTextField(
      modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth(),
      value = uiState.email,
      onValueChange = onEmailChange,
      isError = uiState.emailChangedByUser && emailValidationStatus is ValidationStatus.Error,
      colors = textFieldColors,
      placeholder = { Text(stringResource(CoreResourceR.string.sign_up_your_email)) },
      supportingText = {
        if (uiState.emailChangedByUser && emailValidationStatus is ValidationStatus.Error) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(
              imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_error),
              tint = MVVMJetPackComposeColors.red40,
              contentDescription = null,
            )

            Text(
              text = stringResource(id = emailValidationStatus.getErrorMessage()),
              style = MVVMJetpackComposeTheme.typography.textStyleSmallRegular,
              color = MVVMJetPackComposeColors.red40,
            )
          }
        }
      },
      textStyle = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
      singleLine = true,
      shape = TextFieldShape,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    )

    Text(
      modifier = Modifier.padding(top = 16.dp),
      text = stringResource(CoreResourceR.string.sign_up_password),
      style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )

    CustomizedOutlinedTextField(
      modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth(),
      value = uiState.password,
      onValueChange = onPasswordChange,
      isError = uiState.passwordChangedByUser && passwordValidationStatus is ValidationStatus.Error,
      colors = textFieldColors,
      placeholder = { Text(stringResource(CoreResourceR.string.sign_up_your_password)) },
      supportingText = {
        if (uiState.passwordChangedByUser && passwordValidationStatus is ValidationStatus.Error) {
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
              imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_error),
              tint = MVVMJetPackComposeColors.red40,
              contentDescription = null,
            )

            Text(
              text = stringResource(id = passwordValidationStatus.getErrorMessage()),
              style = MVVMJetpackComposeTheme.typography.textStyleSmallRegular,
              color = MVVMJetPackComposeColors.red40,
            )
          }
        }
      },
      trailingIcon = {
        Icon(
          modifier = Modifier.clickable {
            isPasswordVisible = !isPasswordVisible
          },
          imageVector = if (isPasswordVisible) {
            ImageVector.vectorResource(CoreResourceR.drawable.ic_show_password)
          } else {
            ImageVector.vectorResource(CoreResourceR.drawable.ic_hidden_password)
          },
          tint = MVVMJetPackComposeColors.Neutral20,
          contentDescription = null,
        )
      },
      textStyle = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
      singleLine = true,
      shape = TextFieldShape,
      visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    )

    Text(
      modifier = Modifier
        .padding(top = 16.dp)
        .clickable(onClick = onNavigateToForgotPasswordScreen),
      text = stringResource(CoreResourceR.string.sign_in_forgot_password),
      color = MVVMJetPackComposeColors.Neutral20,
      style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
    )

    Button(
      modifier = Modifier
        .padding(top = 24.dp)
        .fillMaxWidth()
        .height(40.dp),
      shape = TextFieldShape,
      colors = ButtonDefaults.elevatedButtonColors(
        containerColor = MVVMJetPackComposeColors.Primary60,
        contentColor = MVVMJetPackComposeColors.NeutralWhite,
      ),
      onClick = onSignInValidate,
    ) {
      Text(
        text = stringResource(CoreResourceR.string.sign_in_title),
        style = MVVMJetpackComposeTheme.typography.textStyleMediumMedium,
      )
    }

    Row(
      modifier = Modifier
        .padding(vertical = 12.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      HorizontalDivider(
        modifier = Modifier.weight(1f),
      )
      Text(
        modifier = Modifier.padding(all = 12.dp),
        text = stringResource(CoreResourceR.string.sign_in_have_account),
        color = MVVMJetPackComposeColors.Neutral20,
        style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
      )
      HorizontalDivider(modifier = Modifier.weight(1f))
    }

    Text(
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .clickable(onClick = onNavigateToSignUpScreen),
      text = stringResource(CoreResourceR.string.sign_up_title),
      style = MVVMJetpackComposeTheme.typography.textStyleMediumMedium,
      color = MVVMJetPackComposeColors.Neutral10,
      textAlign = TextAlign.Center,
    )
  }
}

@StringRes
private fun ValidationStatus.Error.getErrorMessage(): Int = when (this) {
  ValidationStatus.Error.Email.Empty -> CoreResourceR.string.validation_empty_email
  ValidationStatus.Error.Password.Empty -> CoreResourceR.string.validation_empty_password
}

@Preview
@Composable
private fun SignInPreview() {
  MVVMJetpackComposeTheme {
    Surface {
      SignInContent(
        modifier = Modifier
          .padding(top = 24.dp)
          .padding(horizontal = 24.dp),
        uiState = SignInUiState.initial,
        onEmailChange = {},
        onPasswordChange = {},
        onSignInValidate = {},
        onNavigateToForgotPasswordScreen = {},
        onNavigateToSignUpScreen = {},
      )
    }
  }
}
