package com.vmh.mvvmjetpackcompose.feature.search.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun SearchResultEmpty(keyword: String, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(top = 100.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      modifier = Modifier.size(48.dp),
      painter = painterResource(CoreResourceR.drawable.ic_empty),
      contentDescription = null,
      tint = MVVMJetPackComposeColors.Neutral60,
    )

    Box(modifier = Modifier.height(20.dp))

    Text(
      text = stringResource(CoreResourceR.string.search_result_empty_content, keyword),
      textAlign = TextAlign.Center,
      style = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
      color = MVVMJetPackComposeColors.Neutral20,
    )
  }
}

@Preview
@Composable
private fun SearchResultEmptyPreview() {
  MVVMJetpackComposeTheme {
    Surface {
      SearchResultEmpty(
        keyword = "This is a long search keyword for demo",
      )
    }
  }
}
