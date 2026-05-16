package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
sealed interface SnackbarMessage : SnackbarVisuals {
  data class LabelOnly(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = if (actionLabel == null) {
      SnackbarDuration.Short
    } else {
      SnackbarDuration.Indefinite
    },
  ) : SnackbarMessage

  @Stable
  data class IconAndLabel(
    val icon: Icon,
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = if (actionLabel == null) {
      SnackbarDuration.Short
    } else {
      SnackbarDuration.Indefinite
    },
  ) : SnackbarMessage

  @Immutable
  data class Icon(@DrawableRes val iconResId: Int, val tintColor: Color = Color.Unspecified)
}
