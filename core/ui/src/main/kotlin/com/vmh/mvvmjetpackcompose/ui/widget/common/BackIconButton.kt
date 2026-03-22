package com.vmh.mvvmjetpackcompose.ui.widget.common

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR

@Composable
fun BackIconButton(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
  IconButton(
    modifier = modifier,
    onClick = onBackClick,
  ) {
    Icon(
      painter = painterResource(id = CoreResourceR.drawable.ic_back),
      contentDescription = "",
    )
  }
}
