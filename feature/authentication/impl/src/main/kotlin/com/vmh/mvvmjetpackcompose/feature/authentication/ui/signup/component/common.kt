package com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Stable
internal val TextFieldShape = RoundedCornerShape(size = 12.dp)

@Stable
internal val TextFieldContentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)

internal val textFieldTextStyle
  @Composable
  @ReadOnlyComposable
  get() = MVVMJetpackComposeTheme.typography.textStyleMediumRegular

internal val textFieldColors: TextFieldColors
  @Composable
  get() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MVVMJetPackComposeColors.Neutral10,
    focusedBorderColor = MVVMJetPackComposeColors.Neutral40,
    unfocusedTextColor = MVVMJetPackComposeColors.Neutral10,
    unfocusedBorderColor = MVVMJetPackComposeColors.Neutral70,
    cursorColor = MVVMJetPackComposeColors.NeutralWhite,
    errorCursorColor = MVVMJetPackComposeColors.NeutralWhite,
    focusedPlaceholderColor = MVVMJetPackComposeColors.Neutral40,
    unfocusedPlaceholderColor = MVVMJetPackComposeColors.Neutral40,
    errorTextColor = MVVMJetPackComposeColors.Neutral10,
    errorBorderColor = MVVMJetPackComposeColors.red40,
    errorTrailingIconColor = MVVMJetPackComposeColors.Neutral10,
  )

@Stable
internal val DefaultKeyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)

@Stable
internal val PasswordVisualTransformation = PasswordVisualTransformation()
