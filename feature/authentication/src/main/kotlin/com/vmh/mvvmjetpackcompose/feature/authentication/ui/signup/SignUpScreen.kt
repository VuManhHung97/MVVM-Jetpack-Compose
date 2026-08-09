package com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.DebouncedClickable
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForDialog
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.SignUpSingleEvent
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.SignUpUiState
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.SignUpViewModel
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.ValidationStatus
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.getErrorMessageResId
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthExtendedColors
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthTheme
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.AuthGradientBackground
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.ConfirmPasswordTextField
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.EmailTextField
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.GradientButton
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.PasswordTextField
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.SproutMascot
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent

// Illustration colors used only by SignUp: sprout mascot over a meadow gradient, green CTA depth.
private val SignUpExtendedColors = AuthExtendedColors(
  backgroundTop = Color(0xFFB9F0C8),
  backgroundMid = Color(0xFFDFF9E4),
  backgroundBottom = Color(0xFFEAFBFA),
  ctaGradientTop = Color(0xFF66BB6A),
  ctaShadow = Color(0xFF2E7D32),
  focusAmber = Color(0xFFFFB300),
  matchBorder = Color(0xFF66BB6A),
  mascotBase = Color(0xFF7CB342),
  mascotHighlight = Color(0xFFAEEA00),
  mascotGlow = Color(0x59FFD54F),
  mascotEye = Color(0xFF33470F),
  mascotLeafLight = Color(0xFF81C784),
  mascotLeafDark = Color(0xFF66BB6A),
  cheek = Color(0xFFFF8A65),
)

@Composable
internal fun SignUpScreen(
  onNavigateBack: () -> Unit,
  navigateToAuthenticationScreen: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SignUpViewModel = hiltViewModel(),
) {
  val focusManager = LocalFocusManager.current
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  var appErrorToDisplay by rememberSaveable { mutableStateOf<AppError?>(null) }
  val currentNavigateToAuthenticationScreen by rememberUpdatedState(navigateToAuthenticationScreen)

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      is SignUpSingleEvent.SignUpFailure ->
        appErrorToDisplay = event.error

      SignUpSingleEvent.SignUpSuccess ->
        currentNavigateToAuthenticationScreen()
    }
  }

  AuthTheme(extendedColors = SignUpExtendedColors) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTapGestures { focusManager.clearFocus() } // Clear focus when tapped outside
        },
    ) {
      AuthGradientBackground {
        SignUpContent(
          uiState = uiState,
          onEmailChange = viewModel::onEmailChange,
          onPasswordChange = viewModel::onPasswordChange,
          onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
          onSignUpValidate = viewModel::signUp,
          onNavigateToSignInScreen = { DebouncedClickable.onClick(onNavigateBack) },
        )
      }

      BackIconButton(
        modifier = Modifier
          .statusBarsPadding()
          .padding(start = 6.dp, top = 6.dp),
        onBackClick = { DebouncedClickable.onClick(onNavigateBack) },
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
}

@Composable
private fun SignUpContent(
  uiState: SignUpUiState,
  onEmailChange: (email: String) -> Unit,
  onPasswordChange: (password: String) -> Unit,
  onConfirmPasswordChange: (confirmPassword: String) -> Unit,
  onSignUpValidate: () -> Unit,
  onNavigateToSignInScreen: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
  var isPasswordFocused by remember { mutableStateOf(false) }
  var isConfirmPasswordFocused by remember { mutableStateOf(false) }
  val colorScheme = MaterialTheme.colorScheme

  // Reuse the ViewModel's validation (Valid vs NotMatchWithConfirmPassword) instead of recomputing.
  val confirmMatches = uiState.confirmPassword.isNotEmpty() &&
    uiState.confirmPasswordValidationStatus is ValidationStatus.Valid
  val eyesClosed = (isPasswordFocused || isConfirmPasswordFocused) && !isPasswordVisible

  Column(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 26.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      SproutMascot(eyesClosed = eyesClosed)

      Text(
        modifier = Modifier.padding(top = 20.dp),
        text = stringResource(CoreResourceR.string.auth_sign_up_greeting_title),
        color = colorScheme.onSurface,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
      )
      Text(
        modifier = Modifier.padding(top = 5.dp),
        text = stringResource(CoreResourceR.string.auth_sign_up_greeting_subtitle),
        color = colorScheme.onSurfaceVariant,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
      )
    }

    Column(
      modifier = Modifier
        .padding(top = 22.dp)
        .fillMaxSize()
        .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
        .background(colorScheme.surface)
        .verticalScroll(rememberScrollState())
        .imePadding()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 26.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      EmailTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.email,
        onValueChange = onEmailChange,
        focusColor = colorScheme.tertiary,
        errorMessage = uiState.emailValidationStatus.errorMessageOrNull(uiState.emailChangedByUser),
      )

      PasswordTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.password,
        onValueChange = onPasswordChange,
        placeholder = stringResource(CoreResourceR.string.auth_sign_up_password_placeholder),
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
        onFocusChange = { isPasswordFocused = it },
        errorMessage = uiState.passwordValidationStatus.errorMessageOrNull(uiState.passwordChangedByUser),
      )

      ConfirmPasswordTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.confirmPassword,
        onValueChange = onConfirmPasswordChange,
        isPasswordVisible = isPasswordVisible,
        isMatched = confirmMatches,
        onFocusChange = { isConfirmPasswordFocused = it },
        errorMessage = uiState.confirmPasswordValidationStatus.errorMessageOrNull(uiState.confirmPasswordChangedByUser),
      )

      GradientButton(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        text = stringResource(CoreResourceR.string.auth_sign_up_submit),
        onClick = onSignUpValidate,
        baseColor = colorScheme.tertiary,
      )

      Row(
        modifier = Modifier
          .padding(top = 8.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
      ) {
        Text(
          text = stringResource(CoreResourceR.string.auth_sign_up_have_account),
          color = colorScheme.onSurfaceVariant,
          fontSize = 14.5.sp,
          fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          modifier = Modifier.clickable(onClick = onNavigateToSignInScreen),
          text = stringResource(CoreResourceR.string.auth_sign_up_sign_in_cta),
          color = colorScheme.tertiary,
          fontSize = 14.5.sp,
          fontWeight = FontWeight.ExtraBold,
        )
      }
    }
  }
}

@Composable
private fun ValidationStatus.errorMessageOrNull(changedByUser: Boolean): String? =
  if (changedByUser && this is ValidationStatus.Error) stringResource(getErrorMessageResId()) else null
