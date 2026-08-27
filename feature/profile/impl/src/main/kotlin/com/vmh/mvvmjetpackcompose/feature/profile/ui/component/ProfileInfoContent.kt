package com.vmh.mvvmjetpackcompose.feature.profile.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.AsyncImageWithPlaceholder
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.profile.ui.ProfileUiState
import kotlin.text.isNotEmpty

@Composable
internal fun ProfileInfoContent(item: ProfileUiState.ProfileUiItem.Profile.Info, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    AsyncImageWithPlaceholder(
      modifier = Modifier
        .size(56.dp)
        .clip(CircleShape),
      data = item.url,
      placeholderResId = CoreResourceR.drawable.img_avatar_profile,
    )

    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.Center,
    ) {
      if (item.name.isNotEmpty()) {
        Text(
          text = item.name,
          style = MVVMJetpackComposeTheme.typography.textStyleLargeBold,
          color = MVVMJetPackComposeColors.Neutral10,
        )
      }

      if (item.email.isNotEmpty()) {
        Text(
          text = item.email,
          style = MVVMJetpackComposeTheme.typography.textStyleBaseRegular,
          color = MVVMJetPackComposeColors.Neutral20,
        )
      }
    }

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
private fun ProfileInfoContentPreview() {
  MVVMJetpackComposeTheme {
    ProfileInfoContent(
      item = ProfileUiState.ProfileUiItem.Profile.Info(
        name = "Vu Hung",
        email = "vumhung1997@gmail.com",
        url = "https://avatars.githubusercontent.com/u/10062682?v=4",
      ),
    )
  }
}
