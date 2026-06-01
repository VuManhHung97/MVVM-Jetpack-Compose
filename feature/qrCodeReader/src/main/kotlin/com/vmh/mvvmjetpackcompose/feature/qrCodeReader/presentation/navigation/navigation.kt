package com.vmh.mvvmjetpackcompose.feature.qrCodeReader.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vmh.mvvmjetpackcompose.feature.qrCodeReader.ui.QRCodeReaderRoute

const val QRCodeReaderRoutePattern = "qr_code_reader_route"

fun NavController.navigateToQRCodeReaderScreen(navOptions: NavOptions? = null) = navigate(
  route = QRCodeReaderRoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.qrCodeReaderScreen(onNavigateBack: () -> Unit) {
  composable(route = QRCodeReaderRoutePattern) {
    QRCodeReaderRoute(onNavigateBack = onNavigateBack)
  }
}
