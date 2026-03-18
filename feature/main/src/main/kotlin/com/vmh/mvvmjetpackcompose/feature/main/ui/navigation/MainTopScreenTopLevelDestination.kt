package com.vmh.mvvmjetpackcompose.feature.main.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.feature.home.ui.navigation.HomeGraphRoutePattern
import com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation.ProfileGraphRoutePattern

@Immutable
enum class MainTopScreenTopLevelDestination(
  @DrawableRes val icon: Int,
  val titleTextId: Int,
  val graphRoutePattern: String,
) {
  Home(
    icon = CoreResourceR.drawable.ic_home,
    titleTextId = CoreResourceR.string.tab_home_title,
    graphRoutePattern = HomeGraphRoutePattern,
  ),
  Settings(
    icon = CoreResourceR.drawable.ic_user_24,
    titleTextId = CoreResourceR.string.tab_profile_title,
    graphRoutePattern = ProfileGraphRoutePattern,
  ),
}
