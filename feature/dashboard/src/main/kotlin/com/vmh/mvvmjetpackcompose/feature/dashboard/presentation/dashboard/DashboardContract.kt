package com.vmh.mvvmjetpackcompose.feature.dashboard.presentation.dashboard

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameAccount
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameTransaction
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.TransactionType
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.initialOf
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.signedAmount
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface DashboardUiState {
  companion object {
    val initial: DashboardUiState get() = Loading
  }

  @Immutable
  data object Loading : DashboardUiState

  @Immutable
  data class Content(val recentTransactions: ImmutableList<RecentTransactionUiItem>) : DashboardUiState

  @Immutable
  data class Error(val error: AppError) : DashboardUiState

  @Immutable
  data class RecentTransactionUiItem(
    val id: String,
    val initial: String,
    val username: String,
    val subtitle: String,
    val signedAmount: String,
    val isDeposit: Boolean,
  )
}

fun GameTransaction.toRecentTransactionUiItem(accounts: List<GameAccount>): DashboardUiState.RecentTransactionUiItem {
  val username = accounts.firstOrNull { it.id == accountId }?.username ?: accountId
  return DashboardUiState.RecentTransactionUiItem(
    id = code,
    initial = initialOf(username),
    username = username,
    subtitle = "$time · $method",
    signedAmount = signedAmount(),
    isDeposit = type == TransactionType.Deposit,
  )
}
