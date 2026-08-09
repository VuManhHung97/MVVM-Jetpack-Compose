package com.vmh.mvvmjetpackcompose.feature.account.presentation.accountDetail

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.AccountStatus
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameAccount
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameTransaction
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.TransactionType
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.DepositUiState
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.LockUiState
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.formatXu
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.initialOf
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.signedAmount
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface AccountDetailUiState {
  companion object {
    val initial: AccountDetailUiState get() = Loading
  }

  @Immutable
  data object Loading : AccountDetailUiState

  @Immutable
  data class Content(
    val accountId: String,
    val initial: String,
    val username: String,
    val displayId: String,
    val created: String,
    val status: AccountStatus,
    val balanceFormatted: String,
    val character: String,
    val clan: String,
    val level: Int,
    val vip: String,
    val lastLogin: String,
    val transactions: ImmutableList<DetailTransactionUiItem>,
    val deposit: DepositUiState?,
    val lock: LockUiState?,
  ) : AccountDetailUiState {
    val isActive: Boolean get() = status == AccountStatus.Active
  }

  @Immutable
  data class Error(val error: AppError) : AccountDetailUiState

  @Immutable
  data class DetailTransactionUiItem(
    val code: String,
    val time: String,
    val method: String,
    val signedAmount: String,
    val isDeposit: Boolean,
  )
}

sealed interface AccountDetailSingleEvent {
  data class DepositSuccess(val amountFormatted: String, val username: String) : AccountDetailSingleEvent
  data class LockChanged(val isLocked: Boolean, val username: String) : AccountDetailSingleEvent
}

internal fun GameTransaction.toDetailTransactionUiItem(): AccountDetailUiState.DetailTransactionUiItem =
  AccountDetailUiState.DetailTransactionUiItem(
    code = code,
    time = time,
    method = method,
    signedAmount = signedAmount(),
    isDeposit = type == TransactionType.Deposit,
  )

internal fun GameAccount.toDetailContent(
  transactions: ImmutableList<AccountDetailUiState.DetailTransactionUiItem>,
  deposit: DepositUiState?,
  lock: LockUiState?,
): AccountDetailUiState.Content = AccountDetailUiState.Content(
  accountId = id,
  initial = initialOf(username),
  username = username,
  displayId = id,
  created = created,
  status = status,
  balanceFormatted = formatXu(balance),
  character = character,
  clan = clan,
  level = level,
  vip = vip,
  lastLogin = lastLogin,
  transactions = transactions,
  deposit = deposit,
  lock = lock,
)
