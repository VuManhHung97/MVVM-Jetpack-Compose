package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount.GameAccountResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount.GameTransactionResponse

interface GameAccountRemoteDataSource {
  suspend fun getGameAccounts(): Result<List<GameAccountResponse>, AppError.ApiException>

  suspend fun getGameTransactions(): Result<List<GameTransactionResponse>, AppError.ApiException>

  suspend fun deposit(accountId: String, amount: Long, note: String?): Result<Unit, AppError.ApiException>

  suspend fun setLocked(accountId: String, isLocked: Boolean, reason: String?): Result<Unit, AppError.ApiException>
}
