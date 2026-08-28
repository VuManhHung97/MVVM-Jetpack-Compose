package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SignUpNavKey : NavKey

fun Navigator.navigateToSignUp() = navigate(SignUpNavKey)
