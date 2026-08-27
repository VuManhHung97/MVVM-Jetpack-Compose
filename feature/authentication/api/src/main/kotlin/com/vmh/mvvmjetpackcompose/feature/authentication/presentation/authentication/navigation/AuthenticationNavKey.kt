package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object AuthenticationNavKey : NavKey

fun Navigator.navigateToAuthentication() = navigate(AuthenticationNavKey)
