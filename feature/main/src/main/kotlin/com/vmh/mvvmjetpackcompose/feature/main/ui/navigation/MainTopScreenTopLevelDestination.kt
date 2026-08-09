package com.vmh.mvvmjetpackcompose.feature.main.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.navigation.AccountGraphRoutePattern
import com.vmh.mvvmjetpackcompose.feature.dashboard.presentation.dashboard.navigation.DashboardGraphRoutePattern
import com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation.ProfileGraphRoutePattern
import com.vmh.mvvmjetpackcompose.feature.transaction.presentation.transaction.navigation.TransactionGraphRoutePattern

@Immutable
enum class MainTopScreenTopLevelDestination(
  @param:DrawableRes val iconResId: Int,
  @param:StringRes val titleResId: Int,
  val graphRoutePattern: String,
) {
  Dashboard(R.drawable.ic_tab_dashboard, R.string.tab_dashboard, DashboardGraphRoutePattern),
  Account(R.drawable.ic_tab_account, R.string.tab_account, AccountGraphRoutePattern),
  Transaction(R.drawable.ic_tab_transaction, R.string.tab_transaction, TransactionGraphRoutePattern),
  Profile(R.drawable.ic_tab_admin_profile, R.string.tab_admin_profile, ProfileGraphRoutePattern),
}
