package com.vmh.mvvmjetpackcompose.core.network.remote.interceptor

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.content.getSystemService
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CUSTOM_HEADER
import java.net.HttpURLConnection
import kotlin.apply
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal object ApiConstants {
  object Headers {
    internal const val AUTHORIZATION = "Authorization"
    internal const val BEARER_TOKEN_TYPE = "Bearer"
    const val USER_AGENT = "User-Agent"
    const val DEVICE_PLATFORM = "X-Device-Platform"
    const val APP_VERSION = "x-app-version"
    const val CUSTOM_HEADER = "@"
    const val NO_AUTH = "NoAuth"
    const val CHECK_ACCESS_TOKEN = "CheckAccessToken"
  }

  object DeviceType {
    const val ANDROID_TV = "Android TV"
    const val ANDROID_MOBILE = "Android"
  }
}

private fun bearerToken(token: String) = "${ApiConstants.Headers.BEARER_TOKEN_TYPE} $token"

internal fun Request.Builder.addCommonHeaders(
  token: String?,
  userAgent: String?,
  devicePlatform: String?,
  appVersion: String?,
) = apply {
  if (token != null) {
    addHeader(ApiConstants.Headers.AUTHORIZATION, bearerToken(token))
  }

  // Add user agent header
  if (userAgent != null) {
    addHeader(ApiConstants.Headers.USER_AGENT, userAgent)
  }

  // Add device platform header
  if (devicePlatform != null) {
    addHeader(ApiConstants.Headers.DEVICE_PLATFORM, devicePlatform)
  }

  if (appVersion != null) {
    addHeader(ApiConstants.Headers.APP_VERSION, appVersion)
  }

  removeHeader(CUSTOM_HEADER)
}

internal fun getUserAgent(): String? = System.getProperty("http.agent")

internal fun getDevicePlatform(context: Context): String? {
  val uiModeManager = context.getSystemService<UiModeManager>()

  return when (uiModeManager?.currentModeType) {
    Configuration.UI_MODE_TYPE_TELEVISION -> ApiConstants.DeviceType.ANDROID_TV

    Configuration.UI_MODE_TYPE_NORMAL -> ApiConstants.DeviceType.ANDROID_MOBILE

    else -> null
  }
}

internal fun Context.getApplicationVersionNameOrNull(): String? = try {
  val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
  } else {
    @Suppress("DEPRECATION")
    packageManager.getPackageInfo(packageName, 0)
  }

  packageInfo?.versionName
} catch (_: PackageManager.NameNotFoundException) {
  null
}

internal fun unauthorizedResponse(req: Request) = Response.Builder()
  .code(HttpURLConnection.HTTP_UNAUTHORIZED)
  .message("")
  .body("".toResponseBody())
  .request(req)
  .protocol(Protocol.HTTP_2)
  .build()
