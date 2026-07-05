package com.vmh.mvvmjetpackcompose.feature.account.presentation.accounts

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.AccountStatus
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameAccount
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.DepositUiState
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.LockUiState
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.formatXu
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.initialOf
import kotlinx.collections.immutable.ImmutableList

enum class AccountStatusFilter(@StringRes val labelResId: Int, val status: AccountStatus?) {
  All(R.string.account_filter_all, null),
  Active(R.string.account_filter_active, AccountStatus.Active),
  Locked(R.string.account_filter_locked, AccountStatus.Locked),
}

@Immutable
sealed interface AccountsUiState {
  companion object {
    val initial: AccountsUiState get() = Loading
  }

  @Immutable
  data object Loading : AccountsUiState

  @Immutable
  data class Content(
    val selectedFilter: AccountStatusFilter,
    val accountCount: Int,
    val accounts: ImmutableList<AccountUiItem>,
    val deposit: DepositUiState?,
    val lock: LockUiState?,
  ) : AccountsUiState

  @Immutable
  data class Error(val error: AppError) : AccountsUiState

  @Immutable
  data class AccountUiItem(
    val id: String,
    val initial: String,
    val username: String,
    val character: String,
    val clan: String,
    val level: Int,
    val balanceFormatted: String,
    val status: AccountStatus,
  ) {
    val isActive: Boolean get() = status == AccountStatus.Active
  }
}

sealed interface AccountsSingleEvent {
  data class DepositSuccess(val amountFormatted: String, val username: String) : AccountsSingleEvent
  data class LockChanged(val isLocked: Boolean, val username: String) : AccountsSingleEvent
}

internal fun GameAccount.toAccountUiItem(): AccountsUiState.AccountUiItem = AccountsUiState.AccountUiItem(
  id = id,
  initial = initialOf(username),
  username = username,
  character = character,
  clan = clan,
  level = level,
  balanceFormatted = formatXu(balance),
  status = status,
)
