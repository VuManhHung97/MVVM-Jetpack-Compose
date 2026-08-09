package com.vmh.mvvmjetpackcompose.core.data.repository

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.map
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.data.mapper.gameaccount.toGameAccount
import com.vmh.mvvmjetpackcompose.core.data.mapper.gameaccount.toGameTransaction
import com.vmh.mvvmjetpackcompose.core.domain.repository.GameAccountRepository
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.AccountStatus
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameAccount
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameTransaction
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.TransactionType
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.GameAccountRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Single source of truth for game accounts and Xu transactions, shared across the admin tabs.
 * Data is loaded lazily from [GameAccountRemoteDataSource] (dummy JSON today) and mutations are
 * applied optimistically to the in-memory flows so every tab stays in sync. When a real write API
 * lands, deposit/lock should call the remote source then refresh.
 */
@Singleton
internal class DefaultGameAccountRepository @Inject constructor(
  private val gameAccountRemoteDataSource: GameAccountRemoteDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : GameAccountRepository {

  private val accountsFlow = MutableStateFlow<List<GameAccount>>(emptyList())
  private val transactionsFlow = MutableStateFlow<List<GameTransaction>>(emptyList())

  private val loadMutex = Mutex()
  private var isInitialLoadCompleted = false

  override fun observeAccounts(): Flow<List<GameAccount>> = accountsFlow.onStart { ensureLoaded() }

  override fun observeTransactions(): Flow<List<GameTransaction>> = transactionsFlow.onStart { ensureLoaded() }

  override suspend fun deposit(accountId: String, amount: Long, note: String?): Result<Unit, AppError> =
    withContext(appCoroutineDispatchers.io) {
      if (amount <= 0L) return@withContext Ok(Unit)
      accountsFlow.update { accounts ->
        accounts.map { account ->
          if (account.id == accountId) account.copy(balance = account.balance + amount) else account
        }
      }
      val newTransaction = GameTransaction(
        code = "GD-" + (NEW_TRANSACTION_CODE_BASE + Random.nextInt(NEW_TRANSACTION_CODE_RANGE)),
        accountId = accountId,
        amount = amount,
        method = ADMIN_GRANT_METHOD,
        time = JUST_NOW_TIME,
        type = TransactionType.Deposit,
      )
      transactionsFlow.update { transactions -> listOf(newTransaction) + transactions }
      Ok(Unit)
    }

  override suspend fun setLocked(accountId: String, isLocked: Boolean, reason: String?): Result<Unit, AppError> =
    withContext(appCoroutineDispatchers.io) {
      accountsFlow.update { accounts ->
        accounts.map { account ->
          if (account.id == accountId) {
            account.copy(status = if (isLocked) AccountStatus.Locked else AccountStatus.Active)
          } else {
            account
          }
        }
      }
      Ok(Unit)
    }

  private suspend fun ensureLoaded() {
    loadMutex.withLock {
      if (isInitialLoadCompleted) return
      accountsFlow.value = gameAccountRemoteDataSource.getGameAccounts()
        .map { responses -> responses.map { it.toGameAccount() } }
        .getOrElse { emptyList() }
      transactionsFlow.value = gameAccountRemoteDataSource.getGameTransactions()
        .map { responses -> responses.map { it.toGameTransaction() } }
        .getOrElse { emptyList() }
      isInitialLoadCompleted = true
    }
  }

  private companion object {
    const val NEW_TRANSACTION_CODE_BASE = 88_500
    const val NEW_TRANSACTION_CODE_RANGE = 400
    const val ADMIN_GRANT_METHOD = "Admin cấp"
    const val JUST_NOW_TIME = "Vừa xong"
  }
}
