package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
  CircularProgressIndicator(
    modifier = modifier,
    strokeWidth = 6.dp,
    trackColor = MVVMJetPackComposeColors.TransparentWhite20,
    color = MVVMJetPackComposeColors.NeutralWhite,
  )
}
