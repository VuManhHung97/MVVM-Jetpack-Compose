package com.vmh.mvvmjetpackcompose.feature.qrCodeReader.ui

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface ScanQRState {
  data object NotFound : ScanQRState

  data class Found(val code: String) : ScanQRState
}
