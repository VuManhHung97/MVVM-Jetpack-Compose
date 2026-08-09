package com.vmh.mvvmjetpackcompose.feature.authentication.ui.signin

import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.analytics.TrackScreenViewEvent
import com.vmh.mvvmjetpackcompose.core.ui.common.DebouncedClickable
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForDialog
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.SignInSingleEvent
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.SignInUiState
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.SignInViewModel
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.ValidationStatus
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthExtendedColors
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthTheme
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.AuthGradientBackground
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.EmailTextField
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.GradientButton
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.PasswordTextField
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.SocialButton
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component.SunMascot
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent

private const val SignInScreenName = "SignIn"

// Fixed brand marks for the social sign-in buttons — not part of the app palette.
private val GoogleBrandColor = Color(0xFF4285F4)
private val FacebookBrandColor = Color(0xFF1877F2)

// Illustration colors used only by SignIn: sun mascot over a sky gradient, coral CTA depth.
private val SignInExtendedColors = AuthExtendedColors(
  backgroundTop = Color(0xFF8FE1FF),
  backgroundMid = Color(0xFFC8F3FF),
  backgroundBottom = Color(0xFFEAFBF2),
  ctaGradientTop = Color(0xFFFF8A5C),
  ctaShadow = Color(0xFFE4572E),
  focusAmber = Color(0xFFFFB300),
  matchBorder = Color(0xFF66BB6A),
  mascotBase = Color(0xFFFFC107),
  mascotHighlight = Color(0xFFFFE082),
  mascotGlow = Color(0x59FFD54F),
  mascotEye = Color(0xFF4E342E),
  mascotLeafLight = Color(0xFFFFC107),
  mascotLeafDark = Color(0xFFFFC107),
  cheek = Color(0xFFFF8A65),
)

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
      is SignInSingleEvent.SignInFailure ->
        appErrorToDisplay = event.error

      SignInSingleEvent.SignInSuccess ->
        currentNavigateToAuthenticationScreen()
    }
  }

  AuthTheme(extendedColors = SignInExtendedColors) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTapGestures { focusManager.clearFocus() } // Clear focus when tapped outside
        },
    ) {
      AuthGradientBackground {
        SignInContent(
          uiState = uiState,
          onEmailChange = viewModel::emailChanged,
          onPasswordChange = viewModel::passwordChanged,
          onSignInValidate = viewModel::signIn,
          onNavigateToSignUpScreen = onNavigateToSignUpScreen,
          onForgotPasswordClick = {},
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
private fun SignInContent(
  uiState: SignInUiState,
  onEmailChange: (email: String) -> Unit,
  onPasswordChange: (password: String) -> Unit,
  onSignInValidate: () -> Unit,
  onNavigateToSignUpScreen: () -> Unit,
  onForgotPasswordClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
  var isPasswordFocused by remember { mutableStateOf(false) }
  val colorScheme = MaterialTheme.colorScheme

  Column(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 34.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      SunMascot(eyesClosed = isPasswordFocused && !isPasswordVisible)

      Text(
        modifier = Modifier.padding(top = 22.dp),
        text = stringResource(CoreResourceR.string.auth_sign_in_greeting_title),
        color = colorScheme.onSurface,
        fontSize = 30.sp,
        fontWeight = FontWeight.ExtraBold,
      )
      Text(
        modifier = Modifier.padding(top = 6.dp),
        text = stringResource(CoreResourceR.string.auth_sign_in_greeting_subtitle),
        color = colorScheme.onSurfaceVariant,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
      )
    }

    Column(
      modifier = Modifier
        .padding(top = 26.dp)
        .fillMaxSize()
        .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
        .background(colorScheme.surface)
        .verticalScroll(rememberScrollState())
        .imePadding()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 28.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      EmailTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.email,
        onValueChange = onEmailChange,
        focusColor = colorScheme.secondary,
        errorMessage = uiState.emailValidationStatus.errorMessageOrNull(uiState.emailChangedByUser),
      )

      PasswordTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.password,
        onValueChange = onPasswordChange,
        placeholder = stringResource(CoreResourceR.string.auth_sign_in_password_placeholder),
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
        onFocusChange = { isPasswordFocused = it },
        errorMessage = uiState.passwordValidationStatus.errorMessageOrNull(uiState.passwordChangedByUser),
      )

      Text(
        modifier = Modifier
          .align(Alignment.End)
          .clip(RoundedCornerShape(12.dp))
          .clickable(onClick = onForgotPasswordClick)
          .padding(horizontal = 8.dp, vertical = 6.dp),
        text = stringResource(CoreResourceR.string.auth_sign_in_forgot_password),
        color = colorScheme.secondary,
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
      )

      GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(CoreResourceR.string.auth_sign_in_submit),
        onClick = onSignInValidate,
        baseColor = colorScheme.primary,
      )

      SocialSection()

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier
          .padding(top = 4.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
      ) {
        Text(
          text = stringResource(CoreResourceR.string.auth_sign_in_no_account),
          color = colorScheme.onSurfaceVariant,
          fontSize = 14.5.sp,
          fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          modifier = Modifier.clickable(onClick = onNavigateToSignUpScreen),
          text = stringResource(CoreResourceR.string.auth_sign_in_sign_up_cta),
          color = colorScheme.primary,
          fontSize = 14.5.sp,
          fontWeight = FontWeight.ExtraBold,
        )
      }
    }
  }
}

@Composable
private fun SocialSection(modifier: Modifier = Modifier) {
  val colorScheme = MaterialTheme.colorScheme
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      HorizontalDivider(modifier = Modifier.weight(1f), thickness = 2.dp, color = colorScheme.outlineVariant)
      Text(
        text = stringResource(CoreResourceR.string.auth_sign_in_divider),
        color = colorScheme.outline,
        fontSize = 12.5.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
      )
      HorizontalDivider(modifier = Modifier.weight(1f), thickness = 2.dp, color = colorScheme.outlineVariant)
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      SocialButton(
        modifier = Modifier.weight(1f),
        label = stringResource(CoreResourceR.string.auth_sign_in_google),
        monogram = "G",
        monogramColor = GoogleBrandColor,
        onClick = {},
      )
      SocialButton(
        modifier = Modifier.weight(1f),
        label = stringResource(CoreResourceR.string.auth_sign_in_facebook),
        monogram = "f",
        monogramColor = FacebookBrandColor,
        onClick = {},
      )
    }
  }
}

@Composable
private fun ValidationStatus.errorMessageOrNull(changedByUser: Boolean): String? =
  if (changedByUser && this is ValidationStatus.Error) stringResource(getErrorMessage()) else null

@StringRes
private fun ValidationStatus.Error.getErrorMessage(): Int = when (this) {
  ValidationStatus.Error.Email.Empty -> CoreResourceR.string.validation_empty_email
  ValidationStatus.Error.Password.Empty -> CoreResourceR.string.validation_empty_password
}
