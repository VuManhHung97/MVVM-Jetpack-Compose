package com.vmh.mvvmjetpackcompose.feature.language.presentation.language

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class LanguageUiState(val isLoading: Boolean) : Parcelable {
  companion object {
    val initial
      get() = LanguageUiState(isLoading = false)
  }
}
