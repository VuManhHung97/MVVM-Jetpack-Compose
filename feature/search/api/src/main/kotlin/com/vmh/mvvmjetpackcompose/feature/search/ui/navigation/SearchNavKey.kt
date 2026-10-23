package com.vmh.mvvmjetpackcompose.feature.search.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(val keyword: String? = null) : NavKey

fun Navigator.navigateToSearch(keyword: String? = null) = navigate(SearchNavKey(keyword = keyword))
