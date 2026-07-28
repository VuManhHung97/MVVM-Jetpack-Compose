package com.vmh.mvvmjetpackcompose.feature.camera.ui.camera.component

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.AsyncImageWithPlaceholder
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

private val ActionRowPadding = 24.dp
private val ActionRowSpacing = 16.dp
private val UpdatingIndicatorSize = 24.dp

@Composable
internal fun CapturedPhotoContent(
  photoUri: Uri,
  isUpdating: Boolean,
  onPhotoRetake: () -> Unit,
  onPhotoUpdate: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    AsyncImageWithPlaceholder(
      modifier = Modifier.fillMaxSize(),
      data = photoUri,
      contentScale = ContentScale.Fit,
    )

    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(all = ActionRowPadding),
      horizontalArrangement = Arrangement.spacedBy(space = ActionRowSpacing),
    ) {
      OutlinedButton(
        modifier = Modifier.weight(weight = 1f),
        onClick = onPhotoRetake,
        enabled = !isUpdating,
      ) {
        Text(
          text = stringResource(id = CoreResourceR.string.camera_retake),
          style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
        )
      }

      Button(
        modifier = Modifier.weight(weight = 1f),
        onClick = onPhotoUpdate,
        enabled = !isUpdating,
      ) {
        if (isUpdating) {
          LoadingIndicator(modifier = Modifier.size(size = UpdatingIndicatorSize))
        } else {
          Text(
            text = stringResource(id = CoreResourceR.string.camera_use_photo),
            style = MVVMJetpackComposeTheme.typography.textStyleBaseMedium,
          )
        }
      }
    }
  }
}
