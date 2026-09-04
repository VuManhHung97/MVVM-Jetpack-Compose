package com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SignInNavKey : NavKey

fun Navigator.navigateToSignIn() = navigate(SignInNavKey)
