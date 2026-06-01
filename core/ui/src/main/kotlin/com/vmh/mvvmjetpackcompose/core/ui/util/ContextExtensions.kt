@file:Suppress("TooManyFunctions")

package com.vmh.mvvmjetpackcompose.core.ui.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import timber.log.Timber

//region Picture-in-Picture
/**
 * Returns true if the device supports Picture-in-Picture mode.
 *
 * Minimum supported API: **26 (Android Oreo)**.
 * This checks both the runtime SDK and the system feature flag.
 *
 * [References](https://developer.android.com/develop/ui/views/picture-in-picture)
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun Context.isPictureInPictureFeatureEnabled(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
  packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)

/**
 * Returns true if the device supports *automatic* entry into PiP when your app goes to background.
 *
 * Automatic PiP entry was introduced in **API 31 (Android S)**.
 * Also verifies that the PiP system feature exists.
 *
 * [References](https://developer.android.com/develop/ui/views/picture-in-picture#pip_button)
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun Context.canAutoEnterPictureInPicture(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
  packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)

/**
 * Returns true if this Activity is currently in Picture-in-Picture mode.
 *
 * Requires **API 26 (Android Oreo)** or above to call `isInPictureInPictureMode()`.
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun Activity.isInPictureInPictureModeCompat(): Boolean = isPictureInPictureFeatureEnabled() && isInPictureInPictureMode
//endregion

fun Context.openAppSettings() {
  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    .apply { data = Uri.fromParts("package", packageName, null) }
  startActivity(intent)
}

fun Context.getApplicationVersionNameOrNull(): String? = packageManager.getPackageInfoOrNull(packageName)?.versionName

fun Context.getApplicationLabel(): String = applicationInfo.loadLabel(packageManager).toString()

private fun PackageManager.getPackageInfoOrNull(packageName: String): PackageInfo? = try {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
  } else {
    getPackageInfo(packageName, 0)
  }
} catch (e: PackageManager.NameNotFoundException) {
  Timber.e(e, "Package name not found: $packageName")
  null
}

fun Context.launchUrlInExternalBrowser(url: Uri) {
  runCatching {
    startActivity(Intent(Intent.ACTION_VIEW, url).addCategory(Intent.CATEGORY_BROWSABLE))
  }.onFailure { e ->
    Timber.e(e, "Failed to open url=$url")
  }
}

@SuppressLint("HardwareIds")
fun Context.getAndroidId(): String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

private val AppInPlayStoreDirectionApiUrl = "https://play.google.com/store/apps/details".toUri()

fun Context.openAppInPlayStore() {
  val intent = Intent(
    Intent.ACTION_VIEW,
    AppInPlayStoreDirectionApiUrl
      .buildUpon()
      .appendQueryParameter("id", packageName)
      .build(),
  )
  startActivity(intent)
}

/**
 * Find the closest Activity in a given Context.
 * @throws IllegalStateException if not found
 */
inline fun <reified T : Activity> Context.findActivity(): T {
  var context = this
  while (context is ContextWrapper) {
    if (context is T) return context
    context = context.baseContext
  }
  error("Cannot find activity from context: $this")
}

fun Context.isPermissionGranted(permission: String): Boolean = ContextCompat.checkSelfPermission(this, permission) ==
  PackageManager.PERMISSION_GRANTED
