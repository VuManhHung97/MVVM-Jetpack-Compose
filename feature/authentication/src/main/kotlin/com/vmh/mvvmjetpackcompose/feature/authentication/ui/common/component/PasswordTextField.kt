// Design-handoff pixel/opacity values recreated from the Material 3 SignIn/SignUp mockup.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthTheme

/** Password input row: label, lock-led obscured field with a reveal toggle and inline error. */
@Composable
internal fun PasswordTextField(
  value: String,
  onValueChange: (password: String) -> Unit,
  placeholder: String,
  isPasswordVisible: Boolean,
  onPasswordVisibilityToggle: () -> Unit,
  errorMessage: String?,
  modifier: Modifier = Modifier,
  imeAction: ImeAction = ImeAction.Next,
  onFocusChange: (isFocused: Boolean) -> Unit = {},
) {
  val colorScheme = MaterialTheme.colorScheme
  val toggleDescription = stringResource(CoreResourceR.string.auth_show_password_content_description)
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(7.dp),
  ) {
    Text(
      modifier = Modifier.padding(start = 6.dp),
      text = stringResource(CoreResourceR.string.auth_field_label_password),
      color = colorScheme.onSurface,
      fontSize = 13.5.sp,
      fontWeight = FontWeight.ExtraBold,
    )
    AuthTextField(
      modifier = Modifier.fillMaxWidth(),
      value = value,
      onValueChange = onValueChange,
      leadingEmoji = "🔒",
      placeholder = placeholder,
      focusColor = AuthTheme.extendedColors.focusAmber,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
      isPassword = true,
      isPasswordVisible = isPasswordVisible,
      onFocusChange = onFocusChange,
      trailing = {
        Text(
          modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .clickable(onClick = onPasswordVisibilityToggle)
            .padding(all = 6.dp)
            .semantics { contentDescription = toggleDescription },
          text = if (isPasswordVisible) "🙈" else "👁️",
          fontSize = 19.sp,
        )
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
