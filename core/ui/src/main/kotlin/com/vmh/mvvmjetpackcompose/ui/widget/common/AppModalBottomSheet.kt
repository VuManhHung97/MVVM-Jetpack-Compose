package com.vmh.mvvmjetpackcompose.ui.widget.common

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors

/**
 * Reusable Material 3 modal bottom sheet wrapper. Callers show it conditionally and provide their
 * content; dismissal (scrim tap / drag down) is reported via [onDismissRequest].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalBottomSheet(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  containerColor: Color = MVVMJetPackComposeColors.CardBg,
  content: @Composable ColumnScope.() -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetState = sheetState,
    containerColor = containerColor,
    dragHandle = { BottomSheetDefaults.DragHandle(color = MVVMJetPackComposeColors.BorderSoft) },
  ) {
    content()
  }
}
