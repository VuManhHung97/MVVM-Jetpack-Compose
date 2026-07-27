package com.vmh.mvvmjetpackcompose.core.ui.util

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
sealed interface PermissionStatus : Parcelable {
  @Parcelize
  object Granted : PermissionStatus

  @Parcelize
  data class Denied(val shouldShowRationale: Boolean) : PermissionStatus

  @Parcelize
  object Undefined : PermissionStatus
}
