package com.vmh.mvvmjetpackcompose.feature.search.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SearchNavKey : NavKey

fun Navigator.navigateToSearch() = navigate(SearchNavKey)
