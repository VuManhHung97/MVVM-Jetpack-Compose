package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbarManager = staticCompositionLocalOf<SnackbarManager> {
  error("No SnackbarManager provided")
}

@Composable
fun rememberSnackbarManager(): SnackbarManager = remember {
  SnackbarManager(snackbarHostState = SnackbarHostState())
}

@Stable
class SnackbarManager(val snackbarHostState: SnackbarHostState) {
  suspend fun show(snackbarMessage: SnackbarMessage) {
    snackbarHostState.showSnackbar(snackbarMessage)
  }
}
