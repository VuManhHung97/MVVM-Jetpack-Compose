package com.vmh.mvvmjetpackcompose.feature.authentication.ui.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.AuthenticationSingleEvent
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.AuthenticationViewModel
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle

private val WelcomeSubtitleGray = Color(0xFFCECECE)
private val WelcomeScrim = Color(0x42000000)
private val WelcomeSecondaryButtonBackground = Color(0x26FFFFFF)
private val WelcomeScissorsTint = Color(0xFF1F1F1F)

@Composable
internal fun AuthenticationRoute(
  onNavigateToSignInScreen: () -> Unit,
  onNavigateToSignUpScreen: () -> Unit,
  onNavigateToHomeScreen: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: AuthenticationViewModel = hiltViewModel(),
) {
  val currentOnNavigateToHomeScreen by rememberUpdatedState(onNavigateToHomeScreen)

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      AuthenticationSingleEvent.NavigateToHome ->
        currentOnNavigateToHomeScreen()
    }
  }
  Surface(
    modifier = modifier.fillMaxSize(),
    color = MVVMJetPackComposeColors.NeutralBlack,
  ) {
    AuthenticationContent(
      modifier = Modifier.fillMaxSize(),
      onNavigateToSignInScreen = onNavigateToSignInScreen,
      onNavigateToSignUpScreen = onNavigateToSignUpScreen,
    )
  }
}

@Composable
private fun AuthenticationContent(
  onNavigateToSignInScreen: () -> Unit,
  onNavigateToSignUpScreen: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    Image(
      modifier = Modifier.fillMaxSize(),
      painter = painterResource(id = CoreResourceR.drawable.img_welcome_background),
      contentDescription = null,
      contentScale = ContentScale.Crop,
    )

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(color = WelcomeScrim),
    )

    BrandLogo(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.statusBars)
        .padding(start = 22.dp, top = 32.dp),
    )

    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .padding(horizontal = 22.dp)
        .padding(bottom = 60.dp),
    ) {
      Text(
        text = stringResource(CoreResourceR.string.welcome_subtitle),
        style = MVVMJetpackComposeTheme.typography.textStyleMediumRegular.copy(
          fontSize = 18.sp,
          lineHeight = 20.sp,
        ),
        color = WelcomeSubtitleGray,
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(CoreResourceR.string.welcome_title),
        style = MVVMJetpackComposeTheme.typography.textStyleXXXXLargeBold.copy(
          fontSize = 34.sp,
          lineHeight = 37.sp,
        ),
        color = MVVMJetPackComposeColors.NeutralWhite,
      )

      Spacer(modifier = Modifier.height(48.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(22.dp),
      ) {
        WelcomeButton(
          modifier = Modifier.weight(1f),
          text = stringResource(CoreResourceR.string.welcome_log_in),
          containerColor = MVVMJetPackComposeColors.Orange,
          cornerRadius = 8.dp,
          onClick = onNavigateToSignInScreen,
        )
        WelcomeButton(
          modifier = Modifier.weight(1f),
          text = stringResource(CoreResourceR.string.welcome_sign_up),
          containerColor = WelcomeSecondaryButtonBackground,
          cornerRadius = 12.8.dp,
          onClick = onNavigateToSignUpScreen,
        )
      }
    }
  }
}

@Composable
private fun BrandLogo(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(shape = RoundedCornerShape(size = 4.8.dp))
        .background(color = MVVMJetPackComposeColors.NeutralWhite),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        modifier = Modifier.size(18.dp),
        painter = painterResource(id = CoreResourceR.drawable.ic_scissors),
        contentDescription = null,
        tint = WelcomeScissorsTint,
      )
    }
    Text(
      text = stringResource(CoreResourceR.string.welcome_brand_name),
      style = MVVMJetpackComposeTheme.typography.textStyleXXLargeBold,
      color = MVVMJetPackComposeColors.NeutralWhite,
    )
  }
}

@Composable
private fun WelcomeButton(
  text: String,
  containerColor: Color,
  cornerRadius: Dp,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .height(58.dp)
      .clip(shape = RoundedCornerShape(size = cornerRadius))
      .background(color = containerColor)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      style = MVVMJetpackComposeTheme.typography.textStyleLargeBold.copy(
        fontSize = 17.sp,
        letterSpacing = (-0.41).sp,
      ),
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )
  }
}

@Preview
@Composable
private fun AuthenticationContentPreview() {
  MVVMJetpackComposeTheme {
    AuthenticationContent(
      onNavigateToSignInScreen = {},
      onNavigateToSignUpScreen = {},
    )
  }
}
