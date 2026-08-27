package com.vmh.mvvmjetpackcompose.feature.profile.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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
import com.vmh.mvvmjetpackcompose.feature.profile.ui.ProfileUiState
import kotlin.let

@Composable
internal fun OptionContent(item: ProfileUiState.ProfileUiItem.Option, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    item.iconResId?.let {
      Icon(
        modifier = Modifier.size(24.dp),
        imageVector = ImageVector.vectorResource(it),
        contentDescription = null,
        tint = MVVMJetPackComposeColors.Neutral10,
      )
    }
    Text(
      modifier = Modifier.weight(1f),
      text = stringResource(id = item.textResId),
      style = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
      color = MVVMJetPackComposeColors.Neutral10,
    )
    Icon(
      modifier = Modifier.size(20.dp),
      imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_next),
      contentDescription = null,
      tint = MVVMJetPackComposeColors.Neutral20,
    )
  }
}

@Preview
@Composable
private fun OptionContentPreview() {
  MVVMJetpackComposeTheme {
    OptionContent(item = ProfileUiState.ProfileUiItem.Option.Language)
  }
}
