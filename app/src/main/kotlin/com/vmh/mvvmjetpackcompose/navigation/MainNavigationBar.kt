package com.vmh.mvvmjetpackcompose.navigation

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
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.ui.widget.navigation.NavigationBarItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainNavigationBar(
  destinations: ImmutableList<MainTopScreenTopLevelDestination>,
  currentTopLevelKey: NavKey,
  onDestinationSelect: (destination: MainTopScreenTopLevelDestination) -> Unit,
  onDestinationReselect: (destination: MainTopScreenTopLevelDestination) -> Unit,
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
      val isSelected = destination.navKey == currentTopLevelKey

      NavigationBarItem(
        selected = isSelected,
        onClick = {
          if (isSelected) {
            onDestinationReselect(destination)
          } else {
            onDestinationSelect(destination)
          }
        },
        icon = {
          Icon(
            modifier = Modifier.size(MainNavigationBarIconSize),
            imageVector = ImageVector.vectorResource(id = destination.iconResId),
            contentDescription = null,
          )
        },
        label = {
          Text(
            text = stringResource(destination.titleResId),
            style = if (isSelected) {
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

private val MainNavigationBarIconSize = 24.dp
