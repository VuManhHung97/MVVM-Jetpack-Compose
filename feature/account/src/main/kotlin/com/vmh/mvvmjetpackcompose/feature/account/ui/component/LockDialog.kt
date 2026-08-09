package com.vmh.mvvmjetpackcompose.feature.account.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.LockUiState
import com.vmh.mvvmjetpackcompose.ui.widget.common.DialogCommon
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.AdminTextField

@Composable
internal fun LockDialog(
  state: LockUiState,
  onReasonChange: (String) -> Unit,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  val isLocking = state.isLocking
  DialogCommon(
    onDismiss = onDismiss,
    onClick = onConfirm,
    title = stringResource(if (isLocking) R.string.lock_title else R.string.unlock_title),
    content = stringResource(
      if (isLocking) R.string.lock_description else R.string.unlock_description,
      state.username,
    ),
    cancel = stringResource(R.string.lock_cancel),
    confirm = stringResource(if (isLocking) R.string.lock_confirm else R.string.unlock_confirm),
    containerColor = MVVMJetPackComposeColors.CardBg,
    titleColor = MVVMJetPackComposeColors.Ink,
    contentColor = MVVMJetPackComposeColors.Muted,
    dividerColor = MVVMJetPackComposeColors.Divider,
    cancelBackground = Color.Transparent,
    cancelTextColor = MVVMJetPackComposeColors.Muted,
    confirmTextBackground = if (isLocking) MVVMJetPackComposeColors.Red else MVVMJetPackComposeColors.Green,
    confirmTextColor = MVVMJetPackComposeColors.OnAccent,
    extraContent = if (isLocking) {
      {
        AdminTextField(
          value = state.reason,
          onValueChange = onReasonChange,
          placeholder = stringResource(R.string.lock_reason_placeholder),
          background = MVVMJetPackComposeColors.InputBg,
          fontSize = 13,
          radius = 10.dp,
          paddingVertical = 12.dp,
          paddingHorizontal = 14.dp,
        )
      }
    } else {
      null
    },
  )
}
