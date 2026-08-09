// Design-handoff pixel/opacity values recreated from the Material 3 SignIn/SignUp mockup.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SocialShape = RoundedCornerShape(18.dp)

/** An outlined white "continue with X" social sign-in button with a brand monogram. */
@Composable
internal fun SocialButton(
  label: String,
  monogram: String,
  monogramColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme
  Row(
    modifier = modifier
      .height(52.dp)
      .clip(SocialShape)
      .background(color = colorScheme.surface, shape = SocialShape)
      .border(width = 2.5.dp, color = colorScheme.outlineVariant, shape = SocialShape)
      .clickable(onClick = onClick),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    Text(
      text = monogram,
      color = monogramColor,
      fontSize = 18.sp,
      fontWeight = FontWeight.ExtraBold,
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = label,
      color = colorScheme.onSurface,
      fontSize = 15.sp,
      fontWeight = FontWeight.ExtraBold,
    )
  }
}
