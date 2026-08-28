package com.vmh.mvvmjetpackcompose.feature.profile.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun SignOutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  Button(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.elevatedButtonColors(
      containerColor = MVVMJetPackComposeColors.TransparentWhite5,
      contentColor = MVVMJetPackComposeColors.red40,
    ),
    onClick = onClick,
  ) {
    Row {
      Icon(
        modifier = Modifier.size(24.dp),
        imageVector = ImageVector.vectorResource(CoreResourceR.drawable.ic_setting_sign_out),
        tint = MVVMJetPackComposeColors.red40,
        contentDescription = null,
      )
      Text(
        modifier = Modifier
          .align(Alignment.CenterVertically)
          .padding(start = 8.dp),
        text = stringResource(CoreResourceR.string.profile_sign_out),
        style = MVVMJetpackComposeTheme.typography.textStyleMediumMedium,
      )
    }
  }
}

@Preview
@Composable
private fun SignOutButtonPreview() {
  MVVMJetpackComposeTheme {
    SignOutButton(onClick = {})
  }
}
