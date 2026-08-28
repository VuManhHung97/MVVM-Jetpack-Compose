package com.vmh.mvvmjetpackcompose.feature.home.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object HomeNavKey : NavKey

fun Navigator.navigateToHome() = navigate(HomeNavKey)
