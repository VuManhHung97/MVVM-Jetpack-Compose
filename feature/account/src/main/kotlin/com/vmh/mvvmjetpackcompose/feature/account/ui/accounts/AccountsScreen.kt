// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber", "LongParameterList")

package com.vmh.mvvmjetpackcompose.feature.account.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.feature.account.presentation.accounts.AccountStatusFilter
import com.vmh.mvvmjetpackcompose.feature.account.presentation.accounts.AccountsSingleEvent
import com.vmh.mvvmjetpackcompose.feature.account.presentation.accounts.AccountsUiState
import com.vmh.mvvmjetpackcompose.feature.account.presentation.accounts.AccountsViewModel
import com.vmh.mvvmjetpackcompose.feature.account.ui.component.DepositSheet
import com.vmh.mvvmjetpackcompose.feature.account.ui.component.LockDialog
import com.vmh.mvvmjetpackcompose.feature.account.ui.component.statusMetaOf
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.AdminFilterChip
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.AdminTextField
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GameAdminTopBar
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GoldButton
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.InitialBadge
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.OutlineActionButton
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.StatusPill
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.adminCard
import kotlinx.coroutines.launch

@Composable
internal fun AccountsRoute(
  onNavigateToAccountDetail: (accountId: String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: AccountsViewModel = hiltViewModel(),
  snackbarManager: SnackbarManager = LocalSnackbarManager.current,
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  val query by viewModel.queryStateFlow.collectAsStateWithLifecycle()
  val context = LocalContext.current

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    val message = when (event) {
      is AccountsSingleEvent.DepositSuccess ->
        context.getString(R.string.deposit_success, event.amountFormatted, event.username)

      is AccountsSingleEvent.LockChanged ->
        context.getString(if (event.isLocked) R.string.lock_success else R.string.unlock_success, event.username)
    }
    launch { snackbarManager.show(SnackbarMessage.LabelOnly(message)) }
  }

  AccountsScreen(
    uiState = uiState,
    query = query,
    onQueryChange = viewModel::onQueryChange,
    onQueryClear = viewModel::onQueryClear,
    onStatusFilterSelect = viewModel::onStatusFilterSelect,
    onAccountOpen = onNavigateToAccountDetail,
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
internal fun AccountsScreen(
  uiState: AccountsUiState,
  query: String,
  onQueryChange: (String) -> Unit,
  onQueryClear: () -> Unit,
  onStatusFilterSelect: (AccountStatusFilter) -> Unit,
  onAccountOpen: (String) -> Unit,
  onDepositOpen: (accountId: String, username: String, balanceFormatted: String) -> Unit,
  onLockOpen: (accountId: String, username: String, isCurrentlyActive: Boolean) -> Unit,
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
    GameAdminTopBar(title = stringResource(R.string.account_list_title))
    when (uiState) {
      AccountsUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
      }

      is AccountsUiState.Error -> CommonAppErrorContent(
        modifier = Modifier.fillMaxSize(),
        appError = uiState.error,
        getAppErrorMessage = DefaultGetAppErrorMessageForInline,
      )

      is AccountsUiState.Content -> AccountsContent(
        content = uiState,
        query = query,
        onQueryChange = onQueryChange,
        onQueryClear = onQueryClear,
        onStatusFilterSelect = onStatusFilterSelect,
        onAccountOpen = onAccountOpen,
        onDepositOpen = onDepositOpen,
        onLockOpen = onLockOpen,
      )
    }
  }

  val content = uiState as? AccountsUiState.Content
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
private fun AccountsContent(
  content: AccountsUiState.Content,
  query: String,
  onQueryChange: (String) -> Unit,
  onQueryClear: () -> Unit,
  onStatusFilterSelect: (AccountStatusFilter) -> Unit,
  onAccountOpen: (String) -> Unit,
  onDepositOpen: (accountId: String, username: String, balanceFormatted: String) -> Unit,
  onLockOpen: (accountId: String, username: String, isCurrentlyActive: Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp)
      .padding(top = 10.dp, bottom = 20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    AdminTextField(
      value = query,
      onValueChange = onQueryChange,
      placeholder = stringResource(R.string.account_search_placeholder),
      trailing = {
        if (query.isNotEmpty()) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .clickable(onClick = onQueryClear),
            contentAlignment = Alignment.Center,
          ) {
            Text(text = "✕", color = MVVMJetPackComposeColors.Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      },
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      AccountStatusFilter.entries.forEach { filter ->
        AdminFilterChip(
          label = stringResource(filter.labelResId),
          isSelected = content.selectedFilter == filter,
          onClick = { onStatusFilterSelect(filter) },
        )
      }
      Text(
        text = stringResource(R.string.account_count_suffix, content.accountCount),
        color = MVVMJetPackComposeColors.Muted,
        fontSize = 12.sp,
        textAlign = TextAlign.End,
        modifier = Modifier.weight(1f).padding(start = 8.dp),
      )
    }

    if (content.accounts.isEmpty()) {
      Text(
        text = stringResource(R.string.account_empty),
        color = MVVMJetPackComposeColors.Muted,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
      )
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        content.accounts.forEach { account ->
          AccountCard(
            account = account,
            onOpen = { onAccountOpen(account.id) },
            onDeposit = { onDepositOpen(account.id, account.username, account.balanceFormatted) },
            onLock = { onLockOpen(account.id, account.username, account.isActive) },
          )
        }
      }
    }
  }
}

@Composable
private fun AccountCard(
  account: AccountsUiState.AccountUiItem,
  onOpen: () -> Unit,
  onDeposit: () -> Unit,
  onLock: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val meta = statusMetaOf(account.status)
  Column(
    modifier = modifier
      .fillMaxWidth()
      .adminCard()
      .clickable(onClick = onOpen)
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      InitialBadge(text = account.initial, size = 42, radius = 12, fontSize = 16)
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = account.username,
          color = MVVMJetPackComposeColors.Ink,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = stringResource(R.string.account_subtitle, account.character, account.clan, account.level),
          color = MVVMJetPackComposeColors.Muted,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
      StatusPill(
        text = stringResource(meta.labelResId),
        background = meta.background,
        contentColor = meta.contentColor,
      )
    }
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = stringResource(R.string.account_balance_xu, account.balanceFormatted),
        color = MVVMJetPackComposeColors.GoldValue,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.weight(1f),
      )
      GoldButton(
        text = stringResource(R.string.account_action_deposit),
        onClick = onDeposit,
        fontSize = 12,
        minHeight = 36.dp,
      )
      OutlineActionButton(text = stringResource(meta.lockLabelResId), contentColor = meta.lockColor, onClick = onLock)
    }
  }
}
