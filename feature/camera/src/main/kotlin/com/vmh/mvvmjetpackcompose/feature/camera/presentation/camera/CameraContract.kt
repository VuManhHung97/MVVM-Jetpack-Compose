package com.vmh.mvvmjetpackcompose.feature.camera.presentation.camera

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError

@Immutable
sealed interface CameraUiState {
  companion object {
    val initial: CameraUiState get() = Capturing
  }

  /** The camera preview is running and no photo has been taken yet. */
  @Immutable
  data object Capturing : CameraUiState

  /**
   * A photo has been taken and is being reviewed.
   *
   * @param isUpdating whether [CameraViewModel.updatePhoto] is currently running.
   */
  @Immutable
  data class Captured(val photoUri: Uri, val isUpdating: Boolean) : CameraUiState

  @Immutable
  data class Error(val error: AppError) : CameraUiState
}

sealed interface CameraSingleEvent {
  data object PhotoUpdateSuccess : CameraSingleEvent

  @JvmInline
  value class PhotoUpdateFailure(val error: AppError) : CameraSingleEvent
}
