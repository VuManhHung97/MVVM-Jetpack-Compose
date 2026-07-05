// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber", "LongMethod", "CognitiveComplexMethod", "LongParameterList")

package com.vmh.mvvmjetpackcompose.feature.account.ui.accountDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForInline
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.common.LocalSnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarMessage
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminPageBrush
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminSerif
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.feature.account.presentation.accountDetail.AccountDetailSingleEvent
import com.vmh.mvvmjetpackcompose.feature.account.presentation.accountDetail.AccountDetailUiState
import com.vmh.mvvmjetpackcompose.feature.account.presentation.accountDetail.AccountDetailViewModel
import com.vmh.mvvmjetpackcompose.feature.account.ui.component.DepositSheet
import com.vmh.mvvmjetpackcompose.feature.account.ui.component.LockDialog
import com.vmh.mvvmjetpackcompose.feature.account.ui.component.statusMetaOf
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GameAdminTopBar
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GoldButton
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.KeyValueRow
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.OutlineActionButton
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.SectionTitle
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.SerifValue
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.StatusPill
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.adminCard
import kotlinx.coroutines.launch

@Composable
internal fun AccountDetailRoute(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: AccountDetailViewModel = hiltViewModel(),
  snackbarManager: SnackbarManager = LocalSnackbarManager.current,
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  val context = LocalContext.current

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    val message = when (event) {
      is AccountDetailSingleEvent.DepositSuccess ->
        context.getString(R.string.deposit_success, event.amountFormatted, event.username)

      is AccountDetailSingleEvent.LockChanged ->
        context.getString(if (event.isLocked) R.string.lock_success else R.string.unlock_success, event.username)
    }
    launch { snackbarManager.show(SnackbarMessage.LabelOnly(message)) }
  }

  AccountDetailScreen(
    uiState = uiState,
    onNavigateBack = onNavigateBack,
    onDepositOpen = viewModel::onDepositOpen,
    onLockOpen = viewModel::onLockOpen,
    onDepositClose = viewModel::onDepositClose,
    onDepositAmountChange = viewModel::onDepositAmountChange,
    onDepositQuickSelect = viewModel::onDepositQuickSelect,
    onDepositNoteChange = viewModel::onDepositNoteChange,
    onDepositConfirm = viewModel::onDepositConfirm,
    onLockClose = viewModel::onLockClose,
    onLockReasonChange = viewModel::onLockReasonChange,
    onLockConfirm = viewModel::onLockConfirm,
    modifier = modifier,
  )
}

@Composable
internal fun AccountDetailScreen(
  uiState: AccountDetailUiState,
  onNavigateBack: () -> Unit,
  onDepositOpen: (username: String, balanceFormatted: String) -> Unit,
  onLockOpen: (username: String, isCurrentlyActive: Boolean) -> Unit,
  onDepositClose: () -> Unit,
  onDepositAmountChange: (String) -> Unit,
  onDepositQuickSelect: (Long) -> Unit,
  onDepositNoteChange: (String) -> Unit,
  onDepositConfirm: () -> Unit,
  onLockClose: () -> Unit,
  onLockReasonChange: (String) -> Unit,
  onLockConfirm: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(brush = GameAdminPageBrush),
  ) {
    GameAdminTopBar(
      title = stringResource(R.string.account_detail_title),
      showBack = true,
      onBackClick = onNavigateBack,
    )
    when (uiState) {
      AccountDetailUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
      }

      is AccountDetailUiState.Error -> CommonAppErrorContent(
        modifier = Modifier.fillMaxSize(),
        appError = uiState.error,
        getAppErrorMessage = DefaultGetAppErrorMessageForInline,
      )

      is AccountDetailUiState.Content -> DetailContent(
        content = uiState,
        onDepositOpen = { onDepositOpen(uiState.username, uiState.balanceFormatted) },
        onLockOpen = { onLockOpen(uiState.username, uiState.isActive) },
      )
    }
  }

  val content = uiState as? AccountDetailUiState.Content
  content?.deposit?.let { deposit ->
    DepositSheet(
      state = deposit,
      onDismiss = onDepositClose,
      onAmountChange = onDepositAmountChange,
      onQuickSelect = onDepositQuickSelect,
      onNoteChange = onDepositNoteChange,
      onConfirm = onDepositConfirm,
    )
  }
  content?.lock?.let { lock ->
    LockDialog(
      state = lock,
      onReasonChange = onLockReasonChange,
      onDismiss = onLockClose,
      onConfirm = onLockConfirm,
    )
  }
}

@Composable
private fun DetailContent(
  content: AccountDetailUiState.Content,
  onDepositOpen: () -> Unit,
  onLockOpen: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val meta = statusMetaOf(content.status)
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp)
      .padding(top = 10.dp, bottom = 20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .adminCard(border = MVVMJetPackComposeColors.BorderStrong, radius = 16.dp)
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Box(
        modifier = Modifier
          .size(60.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(color = MVVMJetPackComposeColors.TileBg)
          .border(width = 1.dp, color = MVVMJetPackComposeColors.BorderHover, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
      ) {
        SerifValue(text = content.initial, color = MVVMJetPackComposeColors.Accent, fontSize = 24)
      }
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = content.username,
          color = MVVMJetPackComposeColors.Ink,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = GameAdminSerif,
        )
        Text(
          text = stringResource(R.string.account_detail_id_created, content.displayId, content.created),
          color = MVVMJetPackComposeColors.Muted,
          fontSize = 12.sp,
          modifier = Modifier.padding(top = 2.dp),
        )
      }
      StatusPill(
        text = stringResource(meta.labelResId),
        background = meta.background,
        contentColor = meta.contentColor,
        fontSize = 12,
      )
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .adminCard(
            background = MVVMJetPackComposeColors.InputBg,
            border = MVVMJetPackComposeColors.BorderSoft,
            radius = 12.dp,
          )
          .padding(14.dp),
      ) {
        Text(
          text = stringResource(R.string.account_detail_balance_label),
          color = MVVMJetPackComposeColors.Muted,
          fontSize = 11.sp,
        )
        SerifValue(
          text = stringResource(R.string.account_balance_xu, content.balanceFormatted),
          color = MVVMJetPackComposeColors.GoldValue,
          fontSize = 26,
          modifier = Modifier.padding(top = 2.dp),
        )
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GoldButton(
          text = stringResource(R.string.account_action_deposit),
          onClick = onDepositOpen,
          fontSize = 14,
          minHeight = 44.dp,
          modifier = Modifier.weight(1f),
        )
        OutlineActionButton(
          text = stringResource(meta.lockLabelResId),
          contentColor = meta.lockColor,
          onClick = onLockOpen,
          fontSize = 14,
          minHeight = 44.dp,
          modifier = Modifier.weight(1f),
        )
      }
    }

    Column(modifier = Modifier.fillMaxWidth().adminCard().padding(16.dp)) {
      SectionTitle(
        text = stringResource(R.string.account_detail_character_info_title),
        modifier = Modifier.padding(bottom = 10.dp),
      )
      Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        KeyValueRow(key = stringResource(R.string.account_detail_info_character), value = content.character)
        KeyValueRow(key = stringResource(R.string.account_detail_info_clan), value = content.clan)
        KeyValueRow(
          key = stringResource(R.string.account_detail_info_level),
          value = stringResource(R.string.account_detail_info_level_value, content.level),
        )
        KeyValueRow(key = stringResource(R.string.account_detail_info_vip), value = content.vip)
        KeyValueRow(key = stringResource(R.string.account_detail_info_last_login), value = content.lastLogin)
      }
    }

    Column(modifier = Modifier.fillMaxWidth().adminCard().padding(16.dp)) {
      SectionTitle(
        text = stringResource(R.string.account_detail_transaction_history_title),
        modifier = Modifier.padding(bottom = 6.dp),
      )
      if (content.transactions.isEmpty()) {
        Text(
          text = stringResource(R.string.account_detail_no_transactions),
          color = MVVMJetPackComposeColors.Muted,
          fontSize = 13.sp,
          modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        )
      } else {
        content.transactions.forEach { transaction ->
          Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = transaction.code,
                color = MVVMJetPackComposeColors.Muted,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
              )
              Text(
                text = stringResource(
                  R.string.account_detail_transaction_subtitle,
                  transaction.time,
                  transaction.method,
                ),
                color = MVVMJetPackComposeColors.Faint,
                fontSize = 11.sp,
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              val amountColor = if (transaction.isDeposit) {
                MVVMJetPackComposeColors.GoldValue
              } else {
                MVVMJetPackComposeColors.Red
              }
              val statusColor = if (transaction.isDeposit) {
                MVVMJetPackComposeColors.Green
              } else {
                MVVMJetPackComposeColors.Red
              }
              Text(
                text = transaction.signedAmount,
                color = amountColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
              )
              val statusTextResId = if (transaction.isDeposit) {
                R.string.transaction_status_deposit
              } else {
                R.string.transaction_status_deduct
              }
              Text(
                text = stringResource(statusTextResId),
                color = statusColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
              )
            }
          }
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = MVVMJetPackComposeColors.Divider))
        }
      }
    }
  }
}
