package com.vmh.mvvmjetpackcompose.feature.authentication.ui.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun AuthenticationRoute(onNavigateToSignInScreen: () -> Unit, modifier: Modifier = Modifier) {
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
      AuthenticationContent(
        modifier = Modifier.fillMaxWidth(),
        onNavigateToSignInScreen = onNavigateToSignInScreen,
      )
    }
  }
}

@Composable
private fun AuthenticationContent(onNavigateToSignInScreen: () -> Unit, modifier: Modifier = Modifier) {
  Column(modifier = modifier.padding(top = 100.dp)) {
    Text(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      text = stringResource(CoreResourceR.string.sign_in_title),
      style = MVVMJetpackComposeTheme.typography.textStyleXXXXLargeBold,
      color = MVVMJetPackComposeColors.NeutralWhite,
      textAlign = TextAlign.Center,
    )

    Column(
      modifier = Modifier
        .padding(top = 36.dp)
        .padding(horizontal = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      ContinueWithLoginButton(
        modifier = Modifier.continueWithLoginButtonStyle(onClick = onNavigateToSignInScreen),
        icon = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_email),
        tint = MVVMJetPackComposeColors.Neutral10,
        text = stringResource(CoreResourceR.string.sign_in_email),
      )
    }
  }
}

private fun Modifier.continueWithLoginButtonStyle(onClick: () -> Unit): Modifier = this
  .background(
    color = MVVMJetPackComposeColors.TransparentWhite5,
    shape = RoundedCornerShape(size = 12.dp),
  )
  .clip(shape = RoundedCornerShape(size = 12.dp))
  .clickable(onClick = onClick)
  .padding(horizontal = 14.dp, vertical = 9.dp)
  .fillMaxWidth()

@Composable
private fun ContinueWithLoginButton(
  icon: ImageVector,
  text: String,
  modifier: Modifier = Modifier,
  tint: Color = LocalContentColor.current,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = tint,
    )
    Text(
      modifier = Modifier.weight(1f),
      text = text,
      textAlign = TextAlign.Center,
      style = MVVMJetpackComposeTheme.typography.textStyleMediumMedium,
    )
  }
}

@Preview
@Composable
private fun ContinueWithLoginButtonPreview() {
  MVVMJetpackComposeTheme {
    Surface {
      ContinueWithLoginButton(
        icon = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_email),
        tint = MVVMJetPackComposeColors.NeutralWhite,
        text = stringResource(CoreResourceR.string.sign_in_email),
      )
    }
  }
}
