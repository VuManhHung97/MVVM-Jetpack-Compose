package com.vmh.mvvmjetpackcompose.ui.widget.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.AppErrorMessage
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun AppErrorInlineContent(
  appErrorMessage: AppErrorMessage.Inline,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier.wrapContentHeight(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Icon(
        modifier = Modifier.size(40.dp),
        tint = MVVMJetPackComposeColors.Neutral50,
        contentDescription = null,
        imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_error),
      )

      Text(
        modifier = Modifier.padding(top = 20.dp),
        text = appErrorMessage.title,
        style = MVVMJetpackComposeTheme.typography.textStyleLargeMedium,
        color = MVVMJetPackComposeColors.Neutral10,
      )

      appErrorMessage.message?.let { message ->
        Text(
          modifier = Modifier
            .padding(
              top = 8.dp,
              start = 16.dp,
              end = 16.dp,
            ),
          text = message,
          style = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
          color = MVVMJetPackComposeColors.Neutral20,
        )
      }

      appErrorMessage.positiveButton?.let { title ->
        TextButton(
          modifier = Modifier
            .padding(top = 20.dp)
            .clip(RoundedCornerShape(size = 12.dp))
            .background(MVVMJetPackComposeColors.Neutral10),
          onClick = onRetry,
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

@Preview
@Composable
private fun AppErrorInlineContentPreview() {
  MVVMJetpackComposeTheme {
    AppErrorInlineContent(
      appErrorMessage = AppErrorMessage.badRequestError(),
      onRetry = { },
    )
  }
}
