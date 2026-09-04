package com.vmh.mvvmjetpackcompose.feature.language.ui.language.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun LanguageItem(
  name: String,
  localName: String,
  isSelected: Boolean,
  onLanguageItemClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onLanguageItemClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = name,
        style = MVVMJetpackComposeTheme.typography.textStyleMediumBold,
        color = MVVMJetPackComposeColors.Neutral10,
      )
      Text(
        text = localName,
        style = MVVMJetpackComposeTheme.typography.textStyleSmallRegular,
        color = MVVMJetPackComposeColors.Neutral20,
      )
    }
    if (isSelected) {
      Icon(
        imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_check),
        contentDescription = null,
        tint = MVVMJetPackComposeColors.Neutral10,
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun LanguageItemSelectedPreview() {
  MVVMJetpackComposeTheme {
    LanguageItem(
      name = "English",
      localName = "English",
      isSelected = true,
      onLanguageItemClick = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun LanguageItemUnselectedPreview() {
  MVVMJetpackComposeTheme {
    LanguageItem(
      name = "Español",
      localName = "Spanish",
      isSelected = false,
      onLanguageItemClick = {},
    )
  }
}
