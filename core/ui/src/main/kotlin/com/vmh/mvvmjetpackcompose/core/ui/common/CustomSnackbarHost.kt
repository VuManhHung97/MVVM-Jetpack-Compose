package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.ui.widget.base.CustomSnackBar

@Composable
fun CustomSnackbarHost(snackbarState: SnackbarHostState, modifier: Modifier = Modifier) {
  SnackbarHost(
    modifier = modifier,
    hostState = snackbarState,
  ) { snackbarData ->
    when (val snackbarMessage = snackbarData.visuals) {
      is SnackbarMessage.LabelOnly ->
        CustomSnackBar(message = snackbarMessage.message)

      is SnackbarMessage.IconAndLabel ->
        CustomSnackBar(
          message = snackbarMessage.message,
          leadingContent = {
            Icon(
              modifier = Modifier.size(size = 20.dp),
              imageVector = ImageVector.vectorResource(id = snackbarMessage.icon.iconResId),
              tint = snackbarMessage.icon.tintColor.takeOrElse { LocalContentColor.current },
              contentDescription = null,
            )
          },
        )

      else -> CustomSnackBar(message = snackbarMessage.message)
    }
  }
}
