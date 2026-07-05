package com.vmh.mvvmjetpackcompose.feature.transaction.presentation.transaction

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameAccount
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameTransaction
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.TransactionType
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.initialOf
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.signedAmount
import kotlinx.collections.immutable.ImmutableList

enum class HistoryFilter(@StringRes val labelResId: Int, val type: TransactionType?) {
  All(R.string.transaction_filter_all, null),
  Deposit(R.string.transaction_filter_deposit, TransactionType.Deposit),
  Deduct(R.string.transaction_filter_deduct, TransactionType.Deduct),
}

@Immutable
sealed interface TransactionUiState {
  companion object {
    val initial: TransactionUiState get() = Loading
  }

  @Immutable
  data object Loading : TransactionUiState

  @Immutable
  data class Content(
    val totalDepositFormatted: String,
    val totalDeductFormatted: String,
    val selectedFilter: HistoryFilter,
    val transactions: ImmutableList<TransactionUiItem>,
  ) : TransactionUiState

  @Immutable
  data class Error(val error: AppError) : TransactionUiState

  @Immutable
  data class TransactionUiItem(
    val id: String,
    val initial: String,
    val username: String,
    val code: String,
    val method: String,
    val time: String,
    val signedAmount: String,
    val isDeposit: Boolean,
  )
}

internal fun GameTransaction.toTransactionUiItem(accounts: List<GameAccount>): TransactionUiState.TransactionUiItem {
  val username = accounts.firstOrNull { it.id == accountId }?.username ?: accountId
  return TransactionUiState.TransactionUiItem(
    id = code,
    initial = initialOf(username),
    username = username,
    code = code,
    method = method,
    time = time,
    signedAmount = signedAmount(),
    isDeposit = type == TransactionType.Deposit,
  )
}
