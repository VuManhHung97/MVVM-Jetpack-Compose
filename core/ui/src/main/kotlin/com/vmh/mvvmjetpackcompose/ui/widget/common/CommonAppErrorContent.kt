package com.vmh.mvvmjetpackcompose.ui.widget.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.ui.common.AppErrorMessage
import com.vmh.mvvmjetpackcompose.core.ui.common.GetAppErrorMessage

@Composable
fun CommonAppErrorContent(
  appError: AppError,
  getAppErrorMessage: GetAppErrorMessage,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit = {},
  onConfirm: () -> Unit = {},
  onRetry: () -> Unit = {},
) {
  val appErrorMessage = getAppErrorMessage.from(appError) ?: return

  when (appErrorMessage) {
    is AppErrorMessage.Dialog ->
      AppErrorDialog(
        modifier = modifier,
        appErrorMessage = appErrorMessage,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
      )

    is AppErrorMessage.Inline ->
      AppErrorInlineContent(
        modifier = modifier,
        appErrorMessage = appErrorMessage,
        onRetry = onRetry,
      )
  }
}
