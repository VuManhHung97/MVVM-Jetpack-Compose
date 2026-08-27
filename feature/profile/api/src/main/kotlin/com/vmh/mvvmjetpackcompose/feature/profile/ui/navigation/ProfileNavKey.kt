package com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object ProfileNavKey : NavKey

fun Navigator.navigateToProfile() = navigate(ProfileNavKey)
