package com.vmh.mvvmjetpackcompose.feature.camera.presentation.camera.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vmh.mvvmjetpackcompose.feature.camera.ui.camera.CameraRoute

const val CameraRoutePattern = "camera_route"

fun NavController.navigateToCameraScreen(navOptions: NavOptions? = null) = navigate(
  route = CameraRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.cameraScreen(onNavigateBack: () -> Unit) {
  composable(route = CameraRoutePattern) {
    CameraRoute(onNavigateBack = onNavigateBack)
  }
}
