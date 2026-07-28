package com.vmh.mvvmjetpackcompose.feature.camera.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForDialog
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForInline
import com.vmh.mvvmjetpackcompose.core.ui.common.LocalSnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarMessage
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.core.ui.util.openAppSettings
import com.vmh.mvvmjetpackcompose.feature.camera.presentation.camera.CameraSingleEvent
import com.vmh.mvvmjetpackcompose.feature.camera.presentation.camera.CameraUiState
import com.vmh.mvvmjetpackcompose.feature.camera.presentation.camera.CameraViewModel
import com.vmh.mvvmjetpackcompose.feature.camera.ui.camera.component.CameraPermissionContent
import com.vmh.mvvmjetpackcompose.feature.camera.ui.camera.component.CameraPreview
import com.vmh.mvvmjetpackcompose.feature.camera.ui.camera.component.CapturedPhotoContent
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent

@Composable
internal fun CameraRoute(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: CameraViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val snackbarManager = LocalSnackbarManager.current
  val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)
  var photoUpdateError by rememberSaveable { mutableStateOf<AppError?>(value = null) }
  var hasCameraPermission by rememberSaveable { mutableStateOf(context.hasCameraPermission()) }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
  ) { isGranted ->
    hasCameraPermission = isGranted
  }

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
  }

  // The permission can also be granted from the app settings screen, so re-read it when coming back.
  LifecycleResumeEffect(context) {
    hasCameraPermission = context.hasCameraPermission()
    onPauseOrDispose {}
  }

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      CameraSingleEvent.PhotoUpdateSuccess -> {
        snackbarManager.show(
          snackbarMessage = SnackbarMessage.LabelOnly(
            message = context.getString(CoreResourceR.string.camera_update_success),
          ),
        )
        currentOnNavigateBack()
      }

      is CameraSingleEvent.PhotoUpdateFailure ->
        photoUpdateError = event.error
    }
  }

  CameraScreen(
    uiState = uiState,
    hasCameraPermission = hasCameraPermission,
    onPhotoCapture = viewModel::onPhotoCapture,
    onPhotoCaptureFailure = viewModel::onPhotoCaptureFailure,
    onPhotoRetake = viewModel::onPhotoRetake,
    onPhotoUpdate = viewModel::updatePhoto,
    onAppSettingsOpen = context::openAppSettings,
    onNavigateBack = onNavigateBack,
    modifier = modifier,
  )

  photoUpdateError?.let { error ->
    CommonAppErrorContent(
      appError = error,
      getAppErrorMessage = DefaultGetAppErrorMessageForDialog,
      onDismiss = { photoUpdateError = null },
      onConfirm = { photoUpdateError = null },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraScreen(
  uiState: CameraUiState,
  hasCameraPermission: Boolean,
  onPhotoCapture: (photoUri: Uri) -> Unit,
  onPhotoCaptureFailure: (error: AppError) -> Unit,
  onPhotoRetake: () -> Unit,
  onPhotoUpdate: () -> Unit,
  onAppSettingsOpen: () -> Unit,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val cameraCaptureController = rememberCameraCaptureController()

  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(id = CoreResourceR.string.camera_title),
            style = MVVMJetpackComposeTheme.typography.textStyleXLargeBold,
          )
        },
        navigationIcon = { BackIconButton(onBackClick = onNavigateBack) },
      )
    },
  ) { innerPadding ->
    val contentModifier = Modifier
      .fillMaxSize()
      .padding(paddingValues = innerPadding)
      .consumeWindowInsets(paddingValues = innerPadding)

    if (!hasCameraPermission) {
      CameraPermissionContent(
        modifier = contentModifier,
        onAppSettingsOpen = onAppSettingsOpen,
      )
      return@Scaffold
    }

    when (uiState) {
      CameraUiState.Capturing ->
        CameraPreview(
          modifier = contentModifier,
          controller = cameraCaptureController,
          onPhotoCapture = onPhotoCapture,
          onPhotoCaptureFailure = onPhotoCaptureFailure,
        )

      is CameraUiState.Captured ->
        CapturedPhotoContent(
          modifier = contentModifier,
          photoUri = uiState.photoUri,
          isUpdating = uiState.isUpdating,
          onPhotoRetake = onPhotoRetake,
          onPhotoUpdate = onPhotoUpdate,
        )

      is CameraUiState.Error ->
        CommonAppErrorContent(
          modifier = contentModifier,
          appError = uiState.error,
          getAppErrorMessage = DefaultGetAppErrorMessageForInline,
        )
    }
  }
}

private fun Context.hasCameraPermission(): Boolean =
  ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
