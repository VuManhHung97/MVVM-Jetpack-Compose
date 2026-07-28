package com.vmh.mvvmjetpackcompose.feature.camera.ui.camera

import android.content.Context
import android.hardware.display.DisplayManager
import android.net.Uri
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber

/**
 * Owns the CameraX use cases for the capture screen.
 *
 * CameraX needs a [LifecycleOwner] to bind to, so it lives in the UI layer instead of the ViewModel. The
 * screen only receives the [Uri] of the saved photo and forwards it to the ViewModel.
 *
 * Always create it through [rememberCameraCaptureController] so the background [Executor] and the display
 * rotation listener are released together with the composition.
 */
@Stable
internal class CameraCaptureController(private val context: Context, private val captureExecutor: Executor) {

  val previewUseCase: Preview = Preview.Builder().build()

  private val imageCaptureUseCase: ImageCapture = ImageCapture.Builder()
    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
    .build()

  private var cameraProvider: ProcessCameraProvider? = null

  /**
   * Binds the preview and the capture use cases to [lifecycleOwner].
   *
   * CameraX rejects binding a use case that is already bound, so any previous binding is released first.
   */
  suspend fun bind(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
    val provider = ProcessCameraProvider.awaitInstance(context).also { cameraProvider = it }

    previewUseCase.setSurfaceProvider(surfaceProvider)
    provider.unbindAll()
    provider.bindToLifecycle(
      lifecycleOwner,
      CameraSelector.DEFAULT_BACK_CAMERA,
      previewUseCase,
      imageCaptureUseCase,
    )
  }

  /**
   * Releases the camera while the preview is not shown, e.g. while the captured photo is being reviewed.
   */
  fun unbind() {
    cameraProvider?.unbindAll()
  }

  /**
   * Keeps the saved photo upright when the activity is not recreated on rotation.
   */
  fun onDisplayRotationChange(rotation: Int) {
    imageCaptureUseCase.targetRotation = rotation
  }

  /**
   * Takes a photo and writes it to the app cache directory, so no storage permission is needed.
   */
  suspend fun capture(): Result<Uri, AppError> {
    val photoFile = try {
      createPhotoFile()
    } catch (throwable: SecurityException) {
      Timber.e(throwable, "Failed to create the photo file")
      return Err(AppError.LocalStorageException.FileException(throwable))
    }

    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    return suspendCancellableCoroutine { continuation ->
      imageCaptureUseCase.takePicture(
        outputFileOptions,
        captureExecutor,
        object : ImageCapture.OnImageSavedCallback {
          override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            continuation.resume(Ok(outputFileResults.savedUri ?: Uri.fromFile(photoFile)))
          }

          override fun onError(exception: ImageCaptureException) {
            Timber.e(exception, "takePicture failure")
            continuation.resume(Err(AppError.LocalStorageException.FileException(exception)))
          }
        },
      )
    }
  }

  private fun createPhotoFile(): File {
    val photoDirectory = File(context.cacheDir, PhotoDirectoryName).apply { mkdirs() }
    return File(photoDirectory, "IMG_${System.currentTimeMillis()}.jpg")
  }

  companion object {
    const val PhotoDirectoryName = "photos"
  }
}

@Composable
internal fun rememberCameraCaptureController(): CameraCaptureController {
  val context = LocalContext.current
  val captureExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
  val controller = remember(context, captureExecutor) {
    CameraCaptureController(context = context, captureExecutor = captureExecutor)
  }

  DisposableEffect(captureExecutor) {
    onDispose { captureExecutor.shutdown() }
  }

  DisposableEffect(context, controller) {
    val displayManager = ContextCompat.getSystemService(context, DisplayManager::class.java)
    val displayListener = object : DisplayManager.DisplayListener {
      override fun onDisplayAdded(displayId: Int) = Unit

      override fun onDisplayRemoved(displayId: Int) = Unit

      override fun onDisplayChanged(displayId: Int) {
        val rotation = displayManager?.getDisplay(displayId)?.rotation ?: Surface.ROTATION_0
        controller.onDisplayRotationChange(rotation = rotation)
      }
    }
    displayManager?.registerDisplayListener(displayListener, null)

    onDispose { displayManager?.unregisterDisplayListener(displayListener) }
  }

  return controller
}
