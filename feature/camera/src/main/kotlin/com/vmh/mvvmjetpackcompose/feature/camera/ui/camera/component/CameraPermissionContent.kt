package com.vmh.mvvmjetpackcompose.feature.camera.ui.camera.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

private val ContentPadding = 24.dp
private val ContentSpacing = 16.dp

@Composable
internal fun CameraPermissionContent(onAppSettingsOpen: () -> Unit, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.padding(all = ContentPadding),
    verticalArrangement = Arrangement.spacedBy(space = ContentSpacing, alignment = Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = stringResource(id = CoreResourceR.string.camera_permission_rationale),
      style = MVVMJetpackComposeTheme.typography.textStyleBaseRegular,
      textAlign = TextAlign.Center,
    )

    Button(onClick = onAppSettingsOpen) {
      Text(
        text = stringResource(id = CoreResourceR.string.camera_permission_grant),
        style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
      )
    }
  }
}
