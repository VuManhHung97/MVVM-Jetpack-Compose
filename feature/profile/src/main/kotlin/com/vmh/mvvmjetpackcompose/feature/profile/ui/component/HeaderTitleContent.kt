package com.vmh.mvvmjetpackcompose.feature.profile.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.profile.ui.ProfileUiState.ProfileUiItem

@Composable
internal fun HeaderTitleContent(item: ProfileUiItem.HeaderTitle, modifier: Modifier = Modifier) {
  Text(
    modifier = modifier,
    text = stringResource(item.textResId),
    style = MVVMJetpackComposeTheme.typography.textStyleBaseRegular,
    color = MVVMJetPackComposeColors.Neutral20,
  )
}

@Preview
@Composable
private fun HeaderTitleContentPreview() {
  MVVMJetpackComposeTheme {
    HeaderTitleContent(item = ProfileUiItem.HeaderTitle.Preferences)
  }
}
