// Design-handoff pixel/opacity values recreated from the Material 3 SignIn/SignUp mockup.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthTheme

/**
 * Confirm-password input row. When the value matches the password the border turns to the theme
 * match color and a check mark is shown; it shares its visibility with the password field above.
 */
@Composable
internal fun ConfirmPasswordTextField(
  value: String,
  onValueChange: (confirmPassword: String) -> Unit,
  isPasswordVisible: Boolean,
  isMatched: Boolean,
  errorMessage: String?,
  modifier: Modifier = Modifier,
  onFocusChange: (isFocused: Boolean) -> Unit = {},
) {
  val colorScheme = MaterialTheme.colorScheme
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(7.dp),
  ) {
    Text(
      modifier = Modifier.padding(start = 6.dp),
      text = stringResource(CoreResourceR.string.auth_field_label_confirm_password),
      color = colorScheme.onSurface,
      fontSize = 13.5.sp,
      fontWeight = FontWeight.ExtraBold,
    )
    AuthTextField(
      modifier = Modifier.fillMaxWidth(),
      value = value,
      onValueChange = onValueChange,
      leadingEmoji = "🔐",
      placeholder = stringResource(CoreResourceR.string.auth_sign_up_confirm_password_placeholder),
      focusColor = AuthTheme.extendedColors.focusAmber,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
      isPassword = true,
      isPasswordVisible = isPasswordVisible,
      unfocusedBorderColor = if (isMatched) AuthTheme.extendedColors.matchBorder else colorScheme.outlineVariant,
      onFocusChange = onFocusChange,
      trailing = if (isMatched) {
        { Text(text = "✅", fontSize = 19.sp) }
      } else {
        null
      },
    )
    if (errorMessage != null) {
      Text(
        modifier = Modifier.padding(start = 6.dp),
        text = errorMessage,
        color = colorScheme.error,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
      )
    }
  }
}
