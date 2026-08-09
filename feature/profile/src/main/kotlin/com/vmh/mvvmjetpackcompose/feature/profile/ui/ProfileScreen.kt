// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.common.LocalSnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarMessage
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminPageBrush
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminSerif
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GameAdminTopBar
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.KeyValueRow
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.SectionTitle
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.SerifValue
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.adminCard
import kotlinx.coroutines.launch

@Composable
internal fun ProfileRoute(
  onLogout: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ProfileViewModel = hiltViewModel(),
  snackbarManager: SnackbarManager = LocalSnackbarManager.current,
) {
  val scope = rememberCoroutineScope()
  val logoutFailedMessage = stringResource(R.string.admin_profile_logout_failed)

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      is ProfileSingleEvent.LogoutSuccess -> onLogout()
      is ProfileSingleEvent.LogoutFailure ->
        launch { snackbarManager.show(SnackbarMessage.LabelOnly(logoutFailedMessage)) }
    }
  }

  ProfileScreen(
    onDemoAction = { message -> scope.launch { snackbarManager.show(SnackbarMessage.LabelOnly(message)) } },
    onLogoutClick = viewModel::logout,
    modifier = modifier,
  )
}

private data class ProfileRowUi(val icon: String, val label: String, val demoMessage: String?, val color: Color)

@Composable
internal fun ProfileScreen(onDemoAction: (String) -> Unit, onLogoutClick: () -> Unit, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(brush = GameAdminPageBrush),
  ) {
    GameAdminTopBar(title = stringResource(R.string.admin_profile_title))
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(top = 10.dp, bottom = 20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      HeaderCard()
      InfoCard()
      ActionRowsCard(onDemoAction = onDemoAction, onLogoutClick = onLogoutClick)
    }
  }
}

@Composable
private fun HeaderCard(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxWidth().adminCard(
      border = MVVMJetPackComposeColors.BorderStrong,
      radius = 16.dp,
    ).padding(22.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(color = MVVMJetPackComposeColors.TileBg)
        .border(width = 1.dp, color = MVVMJetPackComposeColors.BorderHover, shape = CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      SerifValue(
        text = stringResource(R.string.game_admin_admin_short),
        color = MVVMJetPackComposeColors.Accent,
        fontSize = 22,
      )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = stringResource(R.string.admin_profile_name),
        color = MVVMJetPackComposeColors.Ink,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = GameAdminSerif,
      )
      Text(
        text = stringResource(R.string.admin_profile_email),
        color = MVVMJetPackComposeColors.Muted,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 2.dp),
      )
    }
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(999.dp))
        .background(color = MVVMJetPackComposeColors.Accent.copy(alpha = 0.14f))
        .border(
          width = 1.dp,
          color = MVVMJetPackComposeColors.AccentDeep.copy(alpha = 0.3f),
          shape = RoundedCornerShape(999.dp),
        )
        .padding(horizontal = 14.dp, vertical = 5.dp),
    ) {
      Text(
        text = stringResource(R.string.admin_profile_role_badge),
        color = MVVMJetPackComposeColors.Accent,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
private fun InfoCard(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth().adminCard().padding(16.dp)) {
    SectionTitle(text = stringResource(R.string.admin_profile_info_title), modifier = Modifier.padding(bottom = 10.dp))
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
      KeyValueRow(
        key = stringResource(R.string.admin_profile_info_role),
        value = stringResource(R.string.admin_profile_info_role_value),
      )
      KeyValueRow(
        key = stringResource(R.string.admin_profile_info_server),
        value = stringResource(R.string.admin_profile_info_server_value),
      )
      KeyValueRow(
        key = stringResource(R.string.admin_profile_info_last_login),
        value = stringResource(R.string.admin_profile_info_last_login_value),
      )
    }
  }
}

@Composable
private fun ActionRowsCard(onDemoAction: (String) -> Unit, onLogoutClick: () -> Unit, modifier: Modifier = Modifier) {
  val rows = listOf(
    ProfileRowUi(
      icon = "✎",
      label = stringResource(R.string.admin_profile_change_password),
      demoMessage = stringResource(R.string.admin_profile_change_password_demo),
      color = MVVMJetPackComposeColors.Ink,
    ),
    ProfileRowUi(
      icon = "⚙",
      label = stringResource(R.string.admin_profile_settings),
      demoMessage = stringResource(R.string.admin_profile_settings_demo),
      color = MVVMJetPackComposeColors.Ink,
    ),
    ProfileRowUi(
      icon = "↪",
      label = stringResource(R.string.admin_profile_logout),
      demoMessage = null,
      color = MVVMJetPackComposeColors.Red,
    ),
  )
  Column(modifier = modifier.fillMaxWidth().adminCard()) {
    rows.forEachIndexed { index, row ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 44.dp)
          .clickable(onClick = { row.demoMessage?.let(onDemoAction) ?: onLogoutClick() })
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(text = row.icon, color = row.color, fontSize = 15.sp, modifier = Modifier.width(20.dp))
        Text(
          text = row.label,
          color = row.color,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.weight(1f),
        )
        Text(text = "›", color = MVVMJetPackComposeColors.Faint, fontSize = 15.sp)
      }
      if (index < rows.lastIndex) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = MVVMJetPackComposeColors.Divider))
      }
    }
  }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
  MVVMJetpackComposeTheme {
    ProfileScreen(onDemoAction = {}, onLogoutClick = {})
  }
}
