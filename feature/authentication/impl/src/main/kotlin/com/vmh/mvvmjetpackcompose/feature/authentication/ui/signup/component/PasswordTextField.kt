package com.vmh.mvvmjetpackcompose.feature.authentication.ui.signup.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.SignUpUiState
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.ValidationStatus
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.getErrorMessageResId
import com.vmh.mvvmjetpackcompose.ui.widget.common.CustomizedOutlinedTextField

@Composable
internal fun PasswordTextField(
  uiState: SignUpUiState,
  isPasswordVisible: Boolean,
  onPasswordChange: (value: String) -> Unit,
  onPasswordVisibilityToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val passwordValidationStatus = uiState.passwordValidationStatus

  CustomizedOutlinedTextField(
    modifier = modifier,
    value = uiState.password,
    onValueChange = onPasswordChange,
    isError = uiState.passwordChangedByUser &&
      passwordValidationStatus is ValidationStatus.Error,
    placeholder = { Text(stringResource(CoreResourceR.string.sign_up_your_password)) },
    supportingText = {
      if (
        uiState.passwordChangedByUser &&
        passwordValidationStatus is ValidationStatus.Error
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(
            imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_error),
            tint = MVVMJetPackComposeColors.red40,
            contentDescription = null,
          )
          Text(
            text = stringResource(
              id = passwordValidationStatus.getErrorMessageResId(),
            ),
            style = MVVMJetpackComposeTheme.typography.textStyleSmallRegular,
            color = MVVMJetPackComposeColors.red40,
          )
        }
      }
    },
    trailingIcon = {
      Icon(
        modifier = Modifier.clickable(onClick = onPasswordVisibilityToggle),
        imageVector = if (isPasswordVisible) {
          ImageVector.vectorResource(id = CoreResourceR.drawable.ic_show_password)
        } else {
          ImageVector.vectorResource(id = CoreResourceR.drawable.ic_hidden_password)
        },
        tint = MVVMJetPackComposeColors.Neutral20,
        contentDescription = null,
      )
    },
    colors = textFieldColors,
    textStyle = textFieldTextStyle,
    shape = TextFieldShape,
    contentPadding = TextFieldContentPadding,
    singleLine = true,
    visualTransformation = if (isPasswordVisible) {
      VisualTransformation.None
    } else {
      PasswordVisualTransformation
    },
    keyboardOptions = DefaultKeyboardOptions,
  )
}
