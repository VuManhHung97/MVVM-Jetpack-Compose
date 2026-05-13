package com.vmh.mvvmjetpackcompose.feature.search.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.ui.widget.common.CustomizedOutlinedTextField
import kotlin.text.isNotEmpty

@Composable
internal fun SearchKeywordTextField(
  value: String,
  onReset: () -> Unit,
  onSubmit: () -> Unit,
  onValueChange: (value: String) -> Unit,
  onFocusChange: (isFocused: Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  CustomizedOutlinedTextField(
    modifier = modifier
      .fillMaxWidth()
      .background(
        color = MVVMJetPackComposeColors.Neutral90,
        shape = RoundedCornerShape(size = 12.dp),
      )
      .onFocusChanged { onFocusChange(it.isFocused) },
    value = value,
    onValueChange = onValueChange,
    placeholder = {
      Text(
        stringResource(CoreResourceR.string.search_hint),
        style = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
        color = MVVMJetPackComposeColors.Neutral40,
      )
    },
    textStyle = MVVMJetpackComposeTheme.typography.textStyleMediumRegular,
    singleLine = true,
    keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    colors = OutlinedTextFieldDefaults.colors(
      focusedTextColor = Color.White,
      cursorColor = Color.White,
      focusedBorderColor = Color.Transparent,
      unfocusedBorderColor = Color.Transparent,
    ),
    contentPadding = PaddingValues(8.dp),
    leadingIcon = {
      Icon(
        modifier = Modifier.size(size = 20.dp),
        imageVector = ImageVector.vectorResource(CoreResourceR.drawable.ic_search),
        tint = MVVMJetPackComposeColors.Neutral20,
        contentDescription = null,
      )
    },
    trailingIcon = {
      if (value.isNotEmpty()) {
        IconButton(onClick = onReset) {
          Icon(
            modifier = Modifier.size(size = 20.dp),
            imageVector = Icons.Default.Close,
            tint = MVVMJetPackComposeColors.Neutral10,
            contentDescription = null,
          )
        }
      }
    },
  )
}

@Composable
@Preview
private fun SearchKeywordTextFieldPreview() {
  MVVMJetpackComposeTheme {
    var value by remember { mutableStateOf("Search keyword") }
    SearchKeywordTextField(
      value = value,
      onValueChange = { value = it },
      onSubmit = {},
      onReset = {},
      onFocusChange = {},
    )
  }
}
