package com.vmh.mvvmjetpackcompose.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import timber.log.Timber

/**
 * Requests the `POST_NOTIFICATIONS` runtime permission once when the app is first shown.
 *
 * A no-op below Android 13, where the permission is granted at install time.
 */
@Composable
internal fun NotificationPermissionEffect() {
  val context = LocalContext.current
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
  ) { isGranted ->
    Timber.d("POST_NOTIFICATIONS permission granted: $isGranted")
  }

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@LaunchedEffect

    val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
      PackageManager.PERMISSION_GRANTED
    if (!isGranted) {
      permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
  }
}
