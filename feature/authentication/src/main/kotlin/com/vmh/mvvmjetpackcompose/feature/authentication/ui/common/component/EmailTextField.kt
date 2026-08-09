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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR

/** Email input row: label, envelope-led field and inline error. [focusColor] is the screen accent. */
@Composable
internal fun EmailTextField(
  value: String,
  onValueChange: (email: String) -> Unit,
  focusColor: Color,
  errorMessage: String?,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(7.dp),
  ) {
    Text(
      modifier = Modifier.padding(start = 6.dp),
      text = stringResource(CoreResourceR.string.auth_field_label_email),
      color = colorScheme.onSurface,
      fontSize = 13.5.sp,
      fontWeight = FontWeight.ExtraBold,
    )
    AuthTextField(
      modifier = Modifier.fillMaxWidth(),
      value = value,
      onValueChange = onValueChange,
      leadingEmoji = "✉️",
      placeholder = stringResource(CoreResourceR.string.auth_email_placeholder),
      focusColor = focusColor,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
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
