// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.ui.widget.gameadmin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminGoldButtonBrush
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminLogoBrush
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminSerif
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors

/** Parchment card surface — background + hairline gold border with rounded corners. */
fun Modifier.adminCard(
  background: Color = MVVMJetPackComposeColors.CardBg,
  border: Color = MVVMJetPackComposeColors.Border,
  radius: Dp = 14.dp,
): Modifier {
  val shape = RoundedCornerShape(radius)
  return clip(shape)
    .background(color = background, shape = shape)
    .border(width = 1.dp, color = border, shape = shape)
}

/** Solid gradient gold button — the primary call to action. */
@Composable
fun GoldButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  fontSize: Int = 14,
  minHeight: Dp = 44.dp,
) {
  Box(
    modifier = modifier
      .heightIn(min = minHeight)
      .clip(RoundedCornerShape(10.dp))
      .alpha(if (enabled) 1f else 0.4f)
      .background(brush = GameAdminGoldButtonBrush)
      .clickable(enabled = enabled, onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = text, color = MVVMJetPackComposeColors.OnAccent, fontSize = fontSize.sp, fontWeight = FontWeight.Bold)
  }
}

/** Transparent bordered secondary button (e.g. Khóa / Mở khóa). */
@Composable
fun OutlineActionButton(
  text: String,
  contentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  fontSize: Int = 12,
  minHeight: Dp = 36.dp,
) {
  Box(
    modifier = modifier
      .heightIn(min = minHeight)
      .clip(RoundedCornerShape(9.dp))
      .border(width = 1.dp, color = MVVMJetPackComposeColors.BorderSoft, shape = RoundedCornerShape(9.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 9.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = text, color = contentColor, fontSize = fontSize.sp, fontWeight = FontWeight.SemiBold)
  }
}

/** Rounded status label (Hoạt động / Đã khóa). */
@Composable
fun StatusPill(
  text: String,
  background: Color,
  contentColor: Color,
  modifier: Modifier = Modifier,
  fontSize: Int = 11,
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(999.dp))
      .background(color = background)
      .padding(horizontal = 10.dp, vertical = 4.dp),
  ) {
    Text(text = text, color = contentColor, fontSize = fontSize.sp, fontWeight = FontWeight.Bold)
  }
}

/** Pill-shaped selectable filter chip. */
@Composable
fun AdminFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  val background = if (isSelected) MVVMJetPackComposeColors.Accent else Color.Transparent
  val contentColor = if (isSelected) MVVMJetPackComposeColors.OnAccent else MVVMJetPackComposeColors.Muted
  val border = if (isSelected) MVVMJetPackComposeColors.Accent else MVVMJetPackComposeColors.BorderSoft
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(999.dp))
      .background(color = background)
      .border(width = 1.dp, color = border, shape = RoundedCornerShape(999.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 9.dp),
  ) {
    Text(text = label, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
  }
}

/** Serif section title used inside cards. */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier, fontSize: Int = 14) {
  Text(
    modifier = modifier,
    text = text,
    color = MVVMJetPackComposeColors.Ink,
    fontSize = fontSize.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = GameAdminSerif,
  )
}

/** Key/value row (label left in muted, value right in bold). */
@Composable
fun KeyValueRow(key: String, value: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(text = key, color = MVVMJetPackComposeColors.Muted, fontSize = 13.sp)
    Text(
      text = value,
      color = MVVMJetPackComposeColors.Ink,
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

/** Extra-bold serif value used for stat numbers / balances. */
@Composable
fun SerifValue(
  text: String,
  color: Color,
  fontSize: Int,
  modifier: Modifier = Modifier,
  fontFamily: FontFamily = GameAdminSerif,
) {
  Text(
    modifier = modifier,
    text = text,
    color = color,
    fontSize = fontSize.sp,
    fontWeight = FontWeight.ExtraBold,
    fontFamily = fontFamily,
  )
}

/** Rounded square avatar showing an account's initial. */
@Composable
fun InitialBadge(text: String, size: Int, radius: Int, fontSize: Int, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .size(size.dp)
      .clip(RoundedCornerShape(radius.dp))
      .background(color = MVVMJetPackComposeColors.TileBg)
      .border(width = 1.dp, color = MVVMJetPackComposeColors.Border, shape = RoundedCornerShape(radius.dp)),
    contentAlignment = Alignment.Center,
  ) {
    SerifValue(text = text, color = MVVMJetPackComposeColors.Accent, fontSize = fontSize)
  }
}

/** "VL" gold logo badge (login + header). */
@Composable
fun LogoBadge(
  size: Int,
  fontSize: Int,
  radius: Int,
  modifier: Modifier = Modifier,
  brush: Brush = GameAdminLogoBrush,
) {
  Box(
    modifier = modifier
      .size(size.dp)
      .clip(RoundedCornerShape(radius.dp))
      .background(brush = brush),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "VL",
      color = MVVMJetPackComposeColors.OnAccent,
      fontSize = fontSize.sp,
      fontWeight = FontWeight.ExtraBold,
      fontFamily = GameAdminSerif,
    )
  }
}

/** Horizontal gold hairline that fades at both ends. */
@Composable
fun GoldDivider(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .background(
        brush = Brush.horizontalGradient(
          0f to MVVMJetPackComposeColors.PageMid.copy(alpha = 0f),
          0.5f to MVVMJetPackComposeColors.AccentDeep.copy(alpha = 0.45f),
          1f to MVVMJetPackComposeColors.PageMid.copy(alpha = 0f),
        ),
      ),
  )
}
