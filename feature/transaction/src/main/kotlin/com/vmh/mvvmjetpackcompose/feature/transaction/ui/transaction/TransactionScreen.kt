// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.transaction.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForInline
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminPageBrush
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.feature.transaction.presentation.transaction.HistoryFilter
import com.vmh.mvvmjetpackcompose.feature.transaction.presentation.transaction.TransactionUiState
import com.vmh.mvvmjetpackcompose.feature.transaction.presentation.transaction.TransactionViewModel
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.AdminFilterChip
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GameAdminTopBar
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.InitialBadge
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.SerifValue
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.adminCard

@Composable
internal fun TransactionRoute(modifier: Modifier = Modifier, viewModel: TransactionViewModel = hiltViewModel()) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  TransactionScreen(
    uiState = uiState,
    onFilterSelect = viewModel::onFilterSelect,
    modifier = modifier,
  )
}

@Composable
internal fun TransactionScreen(
  uiState: TransactionUiState,
  onFilterSelect: (HistoryFilter) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(brush = GameAdminPageBrush),
  ) {
    GameAdminTopBar(title = stringResource(R.string.transaction_title))
    when (uiState) {
      TransactionUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
      }

      is TransactionUiState.Error -> CommonAppErrorContent(
        modifier = Modifier.fillMaxSize(),
        appError = uiState.error,
        getAppErrorMessage = DefaultGetAppErrorMessageForInline,
      )

      is TransactionUiState.Content -> Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          SummaryCard(
            label = stringResource(R.string.transaction_total_deposit_label),
            value = stringResource(R.string.account_balance_xu, uiState.totalDepositFormatted),
            valueColor = MVVMJetPackComposeColors.GoldValue,
            modifier = Modifier.weight(1f),
          )
          SummaryCard(
            label = stringResource(R.string.transaction_total_deduct_label),
            value = stringResource(R.string.account_balance_xu, uiState.totalDeductFormatted),
            valueColor = MVVMJetPackComposeColors.Red,
            modifier = Modifier.weight(1f),
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          HistoryFilter.entries.forEach { filter ->
            AdminFilterChip(
              label = stringResource(filter.labelResId),
              isSelected = uiState.selectedFilter == filter,
              onClick = { onFilterSelect(filter) },
            )
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          uiState.transactions.forEach { transaction -> TransactionRowCard(transaction = transaction) }
        }
      }
    }
  }
}

@Composable
private fun SummaryCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
  Column(modifier = modifier.adminCard().padding(14.dp)) {
    Text(text = label, color = MVVMJetPackComposeColors.Muted, fontSize = 11.sp)
    SerifValue(text = value, color = valueColor, fontSize = 17, modifier = Modifier.padding(top = 4.dp))
  }
}

@Composable
private fun TransactionRowCard(transaction: TransactionUiState.TransactionUiItem, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.fillMaxWidth().adminCard().padding(14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    InitialBadge(text = transaction.initial, size = 38, radius = 11, fontSize = 14)
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = transaction.username,
        color = MVVMJetPackComposeColors.Ink,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = stringResource(R.string.transaction_subtitle, transaction.code, transaction.method, transaction.time),
        color = MVVMJetPackComposeColors.Muted,
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
    Column(horizontalAlignment = Alignment.End) {
      SerifValue(
        text = transaction.signedAmount,
        color = if (transaction.isDeposit) MVVMJetPackComposeColors.GoldValue else MVVMJetPackComposeColors.Red,
        fontSize = 14,
        fontFamily = FontFamily.Default,
      )
      Text(
        text = stringResource(
          if (transaction.isDeposit) R.string.transaction_status_deposit else R.string.transaction_status_deduct,
        ),
        color = if (transaction.isDeposit) MVVMJetPackComposeColors.Green else MVVMJetPackComposeColors.Red,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}
