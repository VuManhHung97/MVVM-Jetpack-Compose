package com.vmh.mvvmjetpackcompose.feature.main.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainTopScreenTopLevelDestination
import com.vmh.mvvmjetpackcompose.ui.widget.navigation.NavigationBarItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainNavigationBar(
  destinations: ImmutableList<MainTopScreenTopLevelDestination>,
  onNavigateToDestination: (MainTopScreenTopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
  modifier: Modifier = Modifier,
) {
  NavigationBar(
    modifier = modifier
      .drawWithContent {
        drawContent()
        drawRect(
          color = MVVMJetPackComposeColors.Neutral90,
          topLeft = Offset(x = 0f, y = 0f),
          size = Size(
            width = size.width,
            height = 1.dp.toPx(),
          ),
        )
      },
    containerColor = MVVMJetPackComposeColors.Neutral100,
  ) {
    destinations.forEach { destination ->
      val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)

      NavigationBarItem(
        selected = selected,
        onClick = { onNavigateToDestination(destination) },
        icon = {
          Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(id = destination.icon),
            contentDescription = null,
          )
        },
        label = {
          Text(
            text = stringResource(destination.titleTextId),
            style = if (selected) {
              MVVMJetpackComposeTheme.typography.textStyleXSmallBold
            } else {
              MVVMJetpackComposeTheme.typography.textStyleXSmallRegular
            },
          )
        },
      )
    }
  }
}
