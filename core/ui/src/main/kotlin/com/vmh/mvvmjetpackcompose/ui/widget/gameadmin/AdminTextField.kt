// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.ui.widget.gameadmin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors

@Composable
fun AdminTextField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  background: Color = MVVMJetPackComposeColors.CardBg,
  border: Color = MVVMJetPackComposeColors.BorderSoft,
  fontSize: Int = 14,
  fontWeight: FontWeight = FontWeight.Normal,
  isPassword: Boolean = false,
  isNumeric: Boolean = false,
  radius: Dp = 12.dp,
  paddingHorizontal: Dp = 16.dp,
  paddingVertical: Dp = 13.dp,
  trailing: (@Composable () -> Unit)? = null,
) {
  val shape = RoundedCornerShape(radius)
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape)
      .background(color = background, shape = shape)
      .border(width = 1.dp, color = border, shape = shape)
      .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(modifier = Modifier.weight(1f)) {
      BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = MVVMJetPackComposeColors.Ink, fontSize = fontSize.sp, fontWeight = fontWeight),
        cursorBrush = SolidColor(MVVMJetPackComposeColors.Accent),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
          keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text,
        ),
      )
      if (value.isEmpty()) {
        Text(
          text = placeholder,
          color = MVVMJetPackComposeColors.Faint,
          fontSize = fontSize.sp,
          fontWeight = fontWeight,
        )
      }
    }
    if (trailing != null) {
      trailing()
    }
  }
}
