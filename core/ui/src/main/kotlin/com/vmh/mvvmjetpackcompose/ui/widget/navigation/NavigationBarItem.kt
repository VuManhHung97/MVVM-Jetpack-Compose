package com.vmh.mvvmjetpackcompose.ui.widget.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors

@Composable
fun RowScope.NavigationBarItem(
  selected: Boolean,
  onClick: () -> Unit,
  icon: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  label: @Composable (() -> Unit)? = null,
  alwaysShowLabel: Boolean = true,
) {
  NavigationBarItem(
    modifier = modifier,
    selected = selected,
    onClick = onClick,
    icon = icon,
    enabled = enabled,
    label = label,
    alwaysShowLabel = alwaysShowLabel,
    colors = getNavigationBarItemColors(),
  )
}

@Composable
private fun getNavigationBarItemColors() = NavigationBarItemDefaults.colors(
  indicatorColor = MVVMJetPackComposeColors.Transparent,
  selectedIconColor = MVVMJetPackComposeColors.Neutral10,
  unselectedIconColor = MVVMJetPackComposeColors.Neutral30,
  selectedTextColor = MVVMJetPackComposeColors.Neutral10,
  unselectedTextColor = MVVMJetPackComposeColors.Neutral30,
)
