package com.vmh.mvvmjetpackcompose.ui.widget.common

import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import kotlin.let

@Suppress("LongParameterList")
@Composable
fun DialogCommon(
  onDismiss: () -> Unit,
  onClick: () -> Unit,
  title: String? = null,
  content: String? = null,
  cancel: String? = null,
  confirm: String? = null,
  @DrawableRes iconIdRes: Int? = null,
  dismissOnBackPress: Boolean = true,
  dismissOnClickOutside: Boolean = true,
  iconTint: Color = MVVMJetPackComposeColors.red40,
  containerColor: Color = MVVMJetPackComposeColors.Neutral90,
  titleColor: Color = MVVMJetPackComposeColors.Neutral10,
  contentColor: Color = MVVMJetPackComposeColors.Neutral20,
  dividerColor: Color = MVVMJetPackComposeColors.TransparentWhite5,
  cancelBackground: Color = MVVMJetPackComposeColors.TransparentWhite5,
  cancelTextColor: Color = MVVMJetPackComposeColors.Neutral10,
  confirmTextBackground: Color = MVVMJetPackComposeColors.Neutral10,
  confirmTextColor: Color = MVVMJetPackComposeColors.Neutral100,
  extraContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
  Dialog(
    onDismissRequest = { onDismiss() },
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnBackPress = dismissOnBackPress,
      dismissOnClickOutside = dismissOnClickOutside,
    ),
  ) {
    val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
    dialogWindowProvider.window.setGravity(Gravity.CENTER)

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(color = MVVMJetPackComposeColors.TransparentBlack60),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 36.dp)
          .background(containerColor, RoundedCornerShape(16.dp))
          .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        iconIdRes?.let {
          Icon(
            modifier = Modifier.size(24.dp),
            tint = iconTint,
            contentDescription = null,
            imageVector = ImageVector.vectorResource(id = iconIdRes),
          )
        }

        title?.let {
          Text(
            modifier = Modifier
              .padding(top = 8.dp)
              .padding(horizontal = 16.dp),
            text = it,
            style = MVVMJetpackComposeTheme.typography.textStyleLargeBold,
            color = titleColor,
          )
        }

        content?.let {
          Text(
            modifier = Modifier
              .padding(top = 8.dp)
              .padding(horizontal = 16.dp),
            text = it,
            textAlign = TextAlign.Center,
            style = MVVMJetpackComposeTheme.typography.textStyleBaseRegular,
            color = contentColor,
          )
        }

        extraContent?.let {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp)
              .padding(horizontal = 16.dp),
            content = it,
          )
        }

        HorizontalDivider(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
          thickness = 1.dp,
          color = dividerColor,
        )

        Row(
          modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          cancel?.let {
            Text(
              modifier = Modifier
                .weight(1f)
                .background(cancelBackground, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(size = 12.dp))
                .clickable {
                  onDismiss()
                }
                .padding(vertical = 9.dp),
              text = it,
              textAlign = TextAlign.Center,
              style = MVVMJetpackComposeTheme.typography.textStyleMediumMedium,
              color = cancelTextColor,
            )
          }

          confirm?.let {
            Text(
              modifier = Modifier
                .weight(1f)
                .background(confirmTextBackground, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(size = 12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 9.dp),
              text = it,
              textAlign = TextAlign.Center,
              style = MVVMJetpackComposeTheme.typography.textStyleMediumMedium,
              color = confirmTextColor,
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun DialogCommonPreview() {
  DialogCommon(
    onDismiss = {},
    onClick = {},
    title = "Title",
    content = "content content content content content content content content content content",
    cancel = "Cancel",
    confirm = "Confirm",
  )
}
