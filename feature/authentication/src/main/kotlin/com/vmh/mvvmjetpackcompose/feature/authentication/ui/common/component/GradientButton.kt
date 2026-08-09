// Design-handoff pixel/opacity values recreated from the Material 3 SignIn/SignUp mockup.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthTheme

private val ButtonShape = RoundedCornerShape(30.dp)
private val ButtonShadowDepth = 6.dp
private val ButtonHeight = 60.dp

/**
 * The primary call-to-action: a vertical gradient face over a colored 3D "lip" that depresses on
 * press. [baseColor] is the screen's accent role (primary / tertiary); the lighter top and the lip
 * come from [AuthTheme.extendedColors].
 */
@Composable
internal fun GradientButton(text: String, onClick: () -> Unit, baseColor: Color, modifier: Modifier = Modifier) {
  val colorScheme = MaterialTheme.colorScheme
  val extendedColors = AuthTheme.extendedColors
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val topOffset by animateDpAsState(
    targetValue = if (isPressed) ButtonShadowDepth else 0.dp,
    label = "button-press",
  )

  Box(modifier = modifier.height(ButtonHeight + ButtonShadowDepth)) {
    // The colored lip that stays put while the face presses down onto it.
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(ButtonHeight)
        .padding(top = ButtonShadowDepth)
        .background(color = extendedColors.ctaShadow, shape = ButtonShape),
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(ButtonHeight)
        .padding(top = topOffset)
        .clip(ButtonShape)
        .background(brush = Brush.verticalGradient(listOf(extendedColors.ctaGradientTop, baseColor)))
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          onClick = onClick,
        ),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = text,
        color = colorScheme.onPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
      )
    }
  }
}
