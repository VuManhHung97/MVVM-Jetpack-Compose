// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.ui.widget.gameadmin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminSerif
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors

/**
 * Shared admin header (VL badge / back arrow, screen title + server subtitle, QT avatar, gold
 * divider) shown at the top of every admin screen.
 */
@Composable
fun GameAdminTopBar(
  title: String,
  modifier: Modifier = Modifier,
  showBack: Boolean = false,
  onBackClick: () -> Unit = {},
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.statusBars),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      if (showBack) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MVVMJetPackComposeColors.TileBg)
            .border(width = 1.dp, color = MVVMJetPackComposeColors.Border, shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onBackClick),
          contentAlignment = Alignment.Center,
        ) {
          Text(text = "‹", color = MVVMJetPackComposeColors.Muted, fontSize = 16.sp)
        }
      } else {
        LogoBadge(size = 36, fontSize = 13, radius = 10)
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          color = MVVMJetPackComposeColors.InkTitle,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = GameAdminSerif,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(text = "Võ Lâm 2 · Máy chủ Tình Nghĩa", color = MVVMJetPackComposeColors.Muted, fontSize = 11.sp)
      }

      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(color = MVVMJetPackComposeColors.TileBg)
          .border(width = 1.dp, color = MVVMJetPackComposeColors.BorderSoft, shape = CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = "QT", color = MVVMJetPackComposeColors.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    GoldDivider(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 4.dp)
        .height(1.dp),
    )
  }
}
