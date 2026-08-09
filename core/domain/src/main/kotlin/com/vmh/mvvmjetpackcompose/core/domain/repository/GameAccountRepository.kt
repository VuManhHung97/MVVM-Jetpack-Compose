package com.vmh.mvvmjetpackcompose.core.domain.repository

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameAccount
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameTransaction
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for game accounts and Xu transactions shared across the admin tabs
 * (Dashboard / Accounts / History). Backed by an in-memory store for now; swap for a remote data
 * source without touching callers.
 */
interface GameAccountRepository {
  fun observeAccounts(): Flow<List<GameAccount>>

  fun observeTransactions(): Flow<List<GameTransaction>>

  suspend fun deposit(accountId: String, amount: Long, note: String?): Result<Unit, AppError>

  suspend fun setLocked(accountId: String, isLocked: Boolean, reason: String?): Result<Unit, AppError>
}
