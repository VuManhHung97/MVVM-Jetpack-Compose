package com.vmh.mvvmjetpackcompose.feature.profile.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.ui.widget.common.DialogCommon

@Composable
internal fun SignOutDialog(onDismiss: () -> Unit, onLogout: () -> Unit) {
  DialogCommon(
    onDismiss = onDismiss,
    onClick = onLogout,
    title = stringResource(CoreResourceR.string.profile_sign_out_are_you_leaving),
    content = stringResource(CoreResourceR.string.profile_sign_out_confirmation),
    cancel = stringResource(CoreResourceR.string.profile_sign_out_cancel),
    confirm = stringResource(CoreResourceR.string.profile_sign_out),
    confirmTextBackground = MVVMJetPackComposeColors.TransparentWhite5,
    confirmTextColor = MVVMJetPackComposeColors.red40,
  )
}

@Preview
@Composable
private fun SignOutDialogPreview() {
  MVVMJetpackComposeTheme {
    SignOutDialog(onDismiss = {}, onLogout = {})
  }
}
