package com.vmh.mvvmjetpackcompose.ui.widget.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
fun CustomSnackBar(
  message: String,
  modifier: Modifier = Modifier,
  containerColor: Color = MVVMJetPackComposeColors.Neutral10,
  leadingContent: @Composable () -> Unit = { },
) {
  Snackbar(
    modifier = modifier
      .padding(20.dp)
      .background(
        color = MVVMJetPackComposeColors.Neutral10,
        shape = RoundedCornerShape(size = 12.dp),
      )
      .clip(RoundedCornerShape(size = 12.dp)),
    containerColor = containerColor,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      leadingContent()

      Text(
        textAlign = TextAlign.Center,
        text = message,
        style = MVVMJetpackComposeTheme.typography.textStyleBaseRegular,
        color = MVVMJetPackComposeColors.Neutral100,
      )
    }
  }
}

@Preview
@Composable
private fun SnackBarPreview() {
  MVVMJetpackComposeTheme {
    Surface {
      CustomSnackBar(
        message = "Comment Added",
        leadingContent = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
      )
    }
  }
}
