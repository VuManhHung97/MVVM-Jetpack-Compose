package com.vmh.mvvmjetpackcompose.ui.widget.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizedOutlinedTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = false,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  minLines: Int = 1,
  interactionSource: MutableInteractionSource? = null,
  shape: Shape = OutlinedTextFieldDefaults.shape,
  colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
  contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
) {
  @Suppress("NAME_SHADOWING")
  val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  // If color is not provided via the text style, use content color as a default
  val textColor =
    textStyle.color.takeOrElse {
      val focused = interactionSource.collectIsFocusedAsState().value
      colors.textColor(enabled, isError, focused)
    }
  val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

  CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
    Column(modifier = modifier) {
      BasicTextField(
        value = value,
        modifier = Modifier.fillMaxWidth(),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError)),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = @Composable { innerTextField ->
          OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            visualTransformation = visualTransformation,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = label,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource,
            colors = colors,
            contentPadding = contentPadding,
            container = {
              OutlinedTextFieldDefaults.Container(
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                shape = shape,
              )
            },
          )
        },
      )

      if (supportingText != null) {
        Spacer(modifier = Modifier.height(6.dp))

        supportingText()
      }
    }
  }
}

/**
 * Represents the color used for the input field of this text field.
 *
 * @param enabled whether the text field is enabled
 * @param isError whether the text field's current value is in error
 * @param focused whether the text field is in focus
 */
@Stable
private fun TextFieldColors.textColor(enabled: Boolean, isError: Boolean, focused: Boolean): Color = when {
  !enabled -> disabledTextColor
  isError -> errorTextColor
  focused -> focusedTextColor
  else -> unfocusedTextColor
}

@Stable
internal fun TextFieldColors.cursorColor(isError: Boolean): Color = if (isError) {
  errorCursorColor
} else {
  cursorColor
}

@Preview(showBackground = true)
@Composable
private fun CustomizedOutlinedTextFieldPreview() {
  MVVMJetpackComposeTheme {
    CustomizedOutlinedTextField(
      value = "Value",
      onValueChange = {},
      label = { Text("Label") },
      placeholder = { Text("Placeholder") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = { Icon(Icons.Default.Close, contentDescription = null) },
      prefix = { Text("Prefix") },
      suffix = { Text("Suffix") },
      supportingText = { Text("Supporting Text") },
      isError = true,
    )
  }
}
