package com.vmh.mvvmjetpackcompose.feature.account.presentation.account

import androidx.compose.runtime.Immutable

/**
 * Deposit bottom-sheet state; null when the sheet is closed. Shared by the accounts list and detail.
 */
@Immutable
data class DepositUiState(
  val accountId: String,
  val username: String,
  val balanceFormatted: String,
  val amount: String,
  val note: String,
) {
  val amountValue: Long get() = amount.filter { it.isDigit() }.toLongOrNull() ?: 0L
}

/** Lock/unlock dialog state; null when the dialog is closed. Shared by the accounts list and detail. */
@Immutable
data class LockUiState(val accountId: String, val username: String, val isLocking: Boolean, val reason: String)
