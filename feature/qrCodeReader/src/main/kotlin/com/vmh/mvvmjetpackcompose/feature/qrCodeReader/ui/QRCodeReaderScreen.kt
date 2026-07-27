package com.vmh.mvvmjetpackcompose.feature.qrCodeReader.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.util.PermissionStatus
import com.vmh.mvvmjetpackcompose.core.ui.util.findActivity
import com.vmh.mvvmjetpackcompose.core.ui.util.isPermissionGranted
import com.vmh.mvvmjetpackcompose.core.ui.util.openAppSettings
import com.vmh.mvvmjetpackcompose.feature.qrCodeReader.ui.component.PreviewFrameQR
import com.vmh.mvvmjetpackcompose.feature.qrCodeReader.ui.component.QrCodeCamera
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.take
import timber.log.Timber

@Composable
internal fun QRCodeReaderRoute(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
  QRCodeReaderScreen(
    onNavigateBack = onNavigateBack,
    modifier = modifier,
  )
}

@Composable
internal fun QRCodeReaderScreen(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  var isCameraEnabled by rememberSaveable { mutableStateOf(false) }
  var permissionStatus by rememberSaveable {
    mutableStateOf<PermissionStatus>(PermissionStatus.Undefined)
  }

  val requestCameraPermission = rememberCameraPermissionRequest(
    onPermissionsResult = { status ->
      isCameraEnabled = status is PermissionStatus.Granted
      permissionStatus = status
    },
  )

  var scanQRState by remember { mutableStateOf<ScanQRState>(ScanQRState.NotFound) }

  LaunchedEffect(Unit) {
    snapshotFlow { scanQRState }
      .filterIsInstance<ScanQRState.Found>()
      .take(1)
      .collect {
        Timber.d("ScanQRState.Found code: ${it.code}")
        isCameraEnabled = false
      }
  }

  Box(modifier = modifier.fillMaxSize()) {
    QrCodeCamera(
      modifier = Modifier.fillMaxSize(),
      isCameraEnabled = isCameraEnabled,
      onScanSuccess = { scanQRState = ScanQRState.Found(it) },
    )

    PreviewFrameQR(
      modifier = Modifier.fillMaxSize(),
      painter = painterResource(id = CoreResourceR.drawable.ic_qrcode),
    )

    when (val status = permissionStatus) {
      is PermissionStatus.Denied ->
        CameraPermissionPlaceholder(
          modifier = Modifier.fillMaxSize(),
          isPermanentlyDenied = !status.shouldShowRationale,
          onRequestPermissionClick = requestCameraPermission,
          onOpenAppSettingsClick = { context.openAppSettings() },
        )

      PermissionStatus.Granted,
      PermissionStatus.Undefined,
      -> Unit
    }

    BackIconButton(
      modifier = Modifier.padding(start = 6.dp, top = 40.dp),
      onBackClick = onNavigateBack,
    )
  }
}

@Composable
private fun CameraPermissionPlaceholder(
  isPermanentlyDenied: Boolean,
  onRequestPermissionClick: () -> Unit,
  onOpenAppSettingsClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = if (isPermanentlyDenied) {
        stringResource(id = CoreResourceR.string.qr_code_reader_permission_denied_message)
      } else {
        stringResource(id = CoreResourceR.string.qr_code_reader_permission_rationale_message)
      },
      textAlign = TextAlign.Center,
    )

    Button(
      onClick = if (isPermanentlyDenied) onOpenAppSettingsClick else onRequestPermissionClick,
    ) {
      Text(
        text = if (isPermanentlyDenied) {
          stringResource(id = CoreResourceR.string.qr_code_reader_permission_open_settings_button)
        } else {
          stringResource(id = CoreResourceR.string.qr_code_reader_permission_request_button)
        },
      )
    }
  }
}

@Composable
private fun rememberCameraPermissionRequest(onPermissionsResult: (PermissionStatus) -> Unit): () -> Unit {
  val context = LocalContext.current
  val currentOnPermissionsResult by rememberUpdatedState(onPermissionsResult)
  var hasRequestedOnce by rememberSaveable { mutableStateOf(false) }

  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission(),
  ) { isGranted ->
    hasRequestedOnce = true
    val permissionStatus = if (isGranted) {
      PermissionStatus.Granted
    } else {
      PermissionStatus.Denied(
        shouldShowRationale = context
          .findActivity<Activity>()
          .shouldShowRequestPermissionRationale(Manifest.permission.CAMERA),
      )
    }

    onPermissionsResult(permissionStatus)
  }

  LaunchedEffect(Unit) {
    context.requestCameraPermission(
      hasRequestedOnce = hasRequestedOnce,
      onPermissionsResult = currentOnPermissionsResult,
      launcher = launcher,
    )
  }

  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME && hasRequestedOnce) {
        context.requestCameraPermission(
          hasRequestedOnce = true,
          onPermissionsResult = currentOnPermissionsResult,
          launcher = launcher,
        )
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  return remember(launcher) {
    {
      currentOnPermissionsResult(PermissionStatus.Undefined)
      launcher.launch(Manifest.permission.CAMERA)
    }
  }
}

private fun Context.requestCameraPermission(
  hasRequestedOnce: Boolean,
  onPermissionsResult: (PermissionStatus) -> Unit,
  launcher: ManagedActivityResultLauncher<String, Boolean>,
) {
  when {
    isPermissionGranted(Manifest.permission.CAMERA) ->
      onPermissionsResult(PermissionStatus.Granted)

    !hasRequestedOnce ->
      launcher.launch(Manifest.permission.CAMERA)

    else -> onPermissionsResult(
      PermissionStatus.Denied(
        shouldShowRationale = findActivity<Activity>()
          .shouldShowRequestPermissionRationale(Manifest.permission.CAMERA),
      ),
    )
  }
}
