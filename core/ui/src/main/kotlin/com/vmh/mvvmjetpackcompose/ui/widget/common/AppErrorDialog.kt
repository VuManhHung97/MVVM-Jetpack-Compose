package com.vmh.mvvmjetpackcompose.ui.widget.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.AppErrorMessage
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun AppErrorDialog(
  appErrorMessage: AppErrorMessage.Dialog,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth()
        .padding(32.dp),
      color = MVVMJetPackComposeColors.Neutral90,
      shape = RoundedCornerShape(size = 16.dp),
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Spacer(modifier = Modifier.height(24.dp))

        Icon(
          modifier = Modifier.size(40.dp),
          tint = MVVMJetPackComposeColors.red40,
          contentDescription = null,
          imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_error),
        )

        Text(
          text = appErrorMessage.title,
          style = MVVMJetpackComposeTheme.typography.textStyleLargeBold,
          color = MVVMJetPackComposeColors.Neutral10,
          textAlign = TextAlign.Center,
        )

        appErrorMessage.message?.let { message ->
          Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = message,
            style = MVVMJetpackComposeTheme.typography.textStyleBaseRegular,
            color = MVVMJetPackComposeColors.Neutral20,
            textAlign = TextAlign.Center,
          )
        }

        appErrorMessage.positiveButton?.let { title ->
          HorizontalDivider(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp),
            thickness = 1.dp,
            color = MVVMJetPackComposeColors.TransparentWhite5,
          )

          TextButton(
            modifier = Modifier
              .padding(12.dp)
              .fillMaxWidth()
              .clip(RoundedCornerShape(size = 12.dp))
              .background(MVVMJetPackComposeColors.Neutral10),
            onClick = onConfirm,
          ) {
            Text(
              style = MVVMJetpackComposeTheme.typography.textStyleMediumMedium,
              color = MVVMJetPackComposeColors.Neutral100,
              text = title,
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun AppErrorDialogPreview() {
  MVVMJetpackComposeTheme {
    AppErrorDialog(
      appErrorMessage = AppErrorMessage.internalServerErrorDialog(),
      onDismiss = { },
      onConfirm = { },
    )
  }
}
