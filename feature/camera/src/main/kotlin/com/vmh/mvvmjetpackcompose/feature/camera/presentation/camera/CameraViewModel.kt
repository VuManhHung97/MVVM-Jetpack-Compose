package com.vmh.mvvmjetpackcompose.feature.camera.presentation.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltViewModel
internal class CameraViewModel @Inject constructor(
  private val eventChannel: EventChannel<CameraSingleEvent>,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : ViewModel(eventChannel),
  HasEventFlow<CameraSingleEvent> by eventChannel {

  private val _uiStateFlow = MutableStateFlow(CameraUiState.initial)

  val uiStateFlow: StateFlow<CameraUiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (CameraUiState) -> CameraUiState) = _uiStateFlow.update(f)

  internal fun onPhotoCapture(photoUri: Uri) {
    emitState { CameraUiState.Captured(photoUri = photoUri, isUpdating = false) }
  }

  internal fun onPhotoCaptureFailure(error: AppError) {
    Timber.e(error, "Failed to capture photo")
    emitState { CameraUiState.Error(error = error) }
  }

  internal fun onPhotoRetake() {
    val captured = _uiStateFlow.value as? CameraUiState.Captured

    emitState { CameraUiState.Capturing }

    captured?.photoUri?.let { photoUri ->
      viewModelScope.launch { deletePhotoFile(photoUri = photoUri) }
    }
  }

  /**
   * Mocks uploading the captured photo: it only waits a moment and then reports success.
   *
   * TODO(vmh): replace this mock with a real `PhotoRepository.updatePhoto(photoUri)` call following
   *  `.claude/rules/data-layer.md` — a repository interface in `core:domain`, a `DefaultPhotoRepository`
   *  in `core:data`, and a `FakePhotoRemoteDataSource` until the backend endpoint exists.
   */
  internal fun updatePhoto() {
    val currentState = _uiStateFlow.value as? CameraUiState.Captured ?: return
    if (currentState.isUpdating) return

    emitState { state -> state.updateCaptured { captured -> captured.copy(isUpdating = true) } }

    viewModelScope.launch {
      delay(timeMillis = MockUpdateDelayMillis)
      Timber.d("Mock updatePhoto with photoUri: ${currentState.photoUri}")

      eventChannel.send(CameraSingleEvent.PhotoUpdateSuccess)
      emitState { state -> state.updateCaptured { captured -> captured.copy(isUpdating = false) } }
    }
  }

  private suspend fun deletePhotoFile(photoUri: Uri) = withContext(appCoroutineDispatchers.io) {
    val path = photoUri.path ?: return@withContext
    runCatching { File(path).delete() }
      .onFailure { throwable -> Timber.e(throwable, "Failed to delete photo file: $path") }
  }

  private companion object {
    private const val MockUpdateDelayMillis = 1_500L
  }
}

private inline fun CameraUiState.updateCaptured(
  transform: (CameraUiState.Captured) -> CameraUiState.Captured,
): CameraUiState = when (this) {
  is CameraUiState.Captured -> transform(this)
  else -> this
}
