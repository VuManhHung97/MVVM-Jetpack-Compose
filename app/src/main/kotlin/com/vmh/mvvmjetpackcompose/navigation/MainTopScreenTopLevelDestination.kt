package com.vmh.mvvmjetpackcompose.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.feature.home.ui.navigation.HomeNavKey
import com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation.ProfileNavKey

@Immutable
enum class MainTopScreenTopLevelDestination(
  @param:DrawableRes val iconResId: Int,
  @param:StringRes val titleResId: Int,
  val navKey: NavKey,
) {
  Home(
    iconResId = CoreResourceR.drawable.ic_home,
    titleResId = CoreResourceR.string.tab_home_title,
    navKey = HomeNavKey,
  ),
  Settings(
    iconResId = CoreResourceR.drawable.ic_user_24,
    titleResId = CoreResourceR.string.tab_profile_title,
    navKey = ProfileNavKey,
  ),
}
