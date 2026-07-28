package com.vmh.mvvmjetpackcompose.feature.camera.ui.camera.component

import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.michaelbull.result.fold
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.feature.camera.ui.camera.CameraCaptureController
import kotlinx.coroutines.launch

private val ShutterButtonSize = 72.dp
private val ShutterButtonBottomPadding = 32.dp

@Composable
internal fun CameraPreview(
  controller: CameraCaptureController,
  onPhotoCapture: (photoUri: Uri) -> Unit,
  onPhotoCaptureFailure: (error: AppError) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val coroutineScope = rememberCoroutineScope()
  var previewView by remember { mutableStateOf<PreviewView?>(value = null) }
  var isCapturing by remember { mutableStateOf(value = false) }

  LaunchedEffect(controller, lifecycleOwner, previewView) {
    val currentPreviewView = previewView ?: return@LaunchedEffect
    controller.bind(
      lifecycleOwner = lifecycleOwner,
      surfaceProvider = currentPreviewView.surfaceProvider,
    )
  }

  DisposableEffect(controller) {
    // The camera stays bound to the lifecycle, so release it explicitly once the preview is gone.
    onDispose { controller.unbind() }
  }

  Box(modifier = modifier) {
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { context ->
        PreviewView(context).apply {
          implementationMode = PreviewView.ImplementationMode.COMPATIBLE
          scaleType = PreviewView.ScaleType.FILL_CENTER
          previewView = this
        }
      },
    )

    IconButton(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = ShutterButtonBottomPadding)
        .size(ShutterButtonSize),
      enabled = !isCapturing,
      onClick = {
        isCapturing = true
        coroutineScope.launch {
          controller.capture().fold(
            success = onPhotoCapture,
            failure = onPhotoCaptureFailure,
          )
          isCapturing = false
        }
      },
    ) {
      Icon(
        imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_camera_shutter),
        contentDescription = stringResource(id = CoreResourceR.string.camera_shutter_content_description),
        tint = Color.Unspecified,
      )
    }
  }
}
