// Design-handoff pixel/opacity values recreated from the Material 3 SignIn/SignUp mockup.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val AuthFieldShape = RoundedCornerShape(20.dp)

/**
 * The base rounded, filled auth input: a leading emoji, focus-aware border/background pulled from
 * [MaterialTheme.colorScheme], and an optional trailing slot. Field-specific composables
 * (email / password / confirm password) wrap this.
 */
@Composable
internal fun AuthTextField(
  value: String,
  onValueChange: (value: String) -> Unit,
  leadingEmoji: String,
  placeholder: String,
  focusColor: Color,
  keyboardOptions: KeyboardOptions,
  modifier: Modifier = Modifier,
  isPassword: Boolean = false,
  isPasswordVisible: Boolean = false,
  unfocusedBorderColor: Color = MaterialTheme.colorScheme.outlineVariant,
  onFocusChange: (isFocused: Boolean) -> Unit = {},
  trailing: @Composable (() -> Unit)? = null,
) {
  val colorScheme = MaterialTheme.colorScheme
  val interactionSource = remember { MutableInteractionSource() }
  val isFocused by interactionSource.collectIsFocusedAsState()
  val borderColor by animateColorAsState(
    targetValue = if (isFocused) focusColor else unfocusedBorderColor,
    label = "field-border",
  )
  val backgroundColor by animateColorAsState(
    targetValue = if (isFocused) colorScheme.surface else colorScheme.surfaceContainer,
    label = "field-background",
  )

  val visualTransformation = if (isPassword && !isPasswordVisible) {
    PasswordVisualTransformation()
  } else {
    VisualTransformation.None
  }

  BasicTextField(
    modifier = modifier
      .onFocusChanged { onFocusChange(it.isFocused) }
      .clip(AuthFieldShape)
      .background(color = backgroundColor, shape = AuthFieldShape)
      .border(width = 2.5.dp, color = borderColor, shape = AuthFieldShape)
      .heightIn(min = 56.dp),
    value = value,
    onValueChange = onValueChange,
    singleLine = true,
    interactionSource = interactionSource,
    cursorBrush = SolidColor(focusColor),
    textStyle = TextStyle(color = colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold),
    visualTransformation = visualTransformation,
    keyboardOptions = keyboardOptions,
    decorationBox = { innerTextField ->
      Row(
        modifier = Modifier.padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = leadingEmoji, fontSize = 19.sp)
        Box(
          modifier = Modifier
            .weight(1f)
            .padding(start = 14.dp),
        ) {
          if (value.isEmpty()) {
            Text(
              text = placeholder,
              color = colorScheme.outline,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
            )
          }
          innerTextField()
        }
        if (trailing != null) {
          Spacer(modifier = Modifier.width(4.dp))
          trailing()
        }
      }
    },
  )
}
