// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainTopScreenTopLevelDestination
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainNavigationBar(
  destinations: ImmutableList<MainTopScreenTopLevelDestination>,
  onNavigateToDestination: (MainTopScreenTopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(color = MVVMJetPackComposeColors.PageTop.copy(alpha = 0.98f))
      .drawBehind {
        drawRect(
          color = MVVMJetPackComposeColors.Border,
          topLeft = Offset.Zero,
          size = Size(width = size.width, height = 1.dp.toPx()),
        )
      }
      .navigationBarsPadding()
      .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp),
  ) {
    destinations.forEach { destination ->
      val isSelected = currentDestination.isTopLevelDestinationInHierarchy(destination)
      val color = if (isSelected) MVVMJetPackComposeColors.Accent else MVVMJetPackComposeColors.Faint
      Column(
        modifier = Modifier
          .weight(1f)
          .heightIn(min = 44.dp)
          .clickable(onClick = { onNavigateToDestination(destination) })
          .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
      ) {
        Icon(
          modifier = Modifier.size(20.dp),
          imageVector = ImageVector.vectorResource(id = destination.iconResId),
          contentDescription = null,
          tint = color,
        )
        Text(
          text = stringResource(destination.titleResId),
          color = color,
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
        )
      }
    }
  }
}
