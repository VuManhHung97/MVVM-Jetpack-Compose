package com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.GameAccountRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount.GameAccountResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount.GameTransactionResponse
import javax.inject.Inject
import kotlinx.coroutines.withContext

/**
 * Serves dummy JSON bundled under `resources/dummy/` — a stand-in for the real API until the backend
 * is ready. The JSON shape matches [GameAccountApiService] responses so swapping in the real data
 * source requires no caller changes.
 */
internal class FakeGameAccountRemoteDataSourceImpl @Inject constructor(
  private val moshi: Moshi,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : GameAccountRemoteDataSource {

  override suspend fun getGameAccounts(): Result<List<GameAccountResponse>, AppError.ApiException> =
    withContext(appCoroutineDispatchers.io) {
      Ok(readJsonList(fileName = ACCOUNTS_FILE_NAME, itemType = GameAccountResponse::class.java))
    }

  override suspend fun getGameTransactions(): Result<List<GameTransactionResponse>, AppError.ApiException> =
    withContext(appCoroutineDispatchers.io) {
      Ok(readJsonList(fileName = TRANSACTIONS_FILE_NAME, itemType = GameTransactionResponse::class.java))
    }

  override suspend fun deposit(accountId: String, amount: Long, note: String?): Result<Unit, AppError.ApiException> =
    Ok(Unit)

  override suspend fun setLocked(
    accountId: String,
    isLocked: Boolean,
    reason: String?,
  ): Result<Unit, AppError.ApiException> = Ok(Unit)

  private fun <T> readJsonList(fileName: String, itemType: Class<T>): List<T> {
    val listType = Types.newParameterizedType(List::class.java, itemType)
    val adapter = moshi.adapter<List<T>>(listType)
    val json = requireNotNull(javaClass.classLoader?.getResourceAsStream(fileName)).bufferedReader().use {
      it.readText()
    }
    return adapter.fromJson(json).orEmpty()
  }

  private companion object {
    const val ACCOUNTS_FILE_NAME = "dummy/game_accounts.json"
    const val TRANSACTIONS_FILE_NAME = "dummy/game_transactions.json"
  }
}
