package com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.component

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun SignUpButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  Button(
    modifier = modifier,
    shape = TextFieldShape,
    colors = ButtonDefaults.elevatedButtonColors(
      containerColor = MVVMJetPackComposeColors.Primary60,
      contentColor = MVVMJetPackComposeColors.NeutralWhite,
    ),
    onClick = onClick,
  ) {
    Text(
      text = stringResource(CoreResourceR.string.sign_up_title),
      style = MVVMJetpackComposeTheme.typography.textStyleMediumMedium,
    )
  }
}

@Preview
@Composable
private fun SignUpButtonPreview() {
  MVVMJetpackComposeTheme {
    SignUpButton(onClick = {})
  }
}
