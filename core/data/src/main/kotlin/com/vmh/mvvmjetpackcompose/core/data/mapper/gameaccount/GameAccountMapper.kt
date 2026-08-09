package com.vmh.mvvmjetpackcompose.core.data.mapper.gameaccount

import com.vmh.mvvmjetpackcompose.core.model.gameaccount.AccountStatus
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameAccount
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameTransaction
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.TransactionType
import com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount.GameAccountResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount.GameTransactionResponse

private const val STATUS_LOCKED = "locked"
private const val TRANSACTION_TYPE_DEDUCT = "deduct"

internal fun GameAccountResponse.toGameAccount(): GameAccount = GameAccount(
  id = id,
  username = username,
  character = character,
  clan = clan,
  level = level,
  balance = balance,
  status = if (status == STATUS_LOCKED) AccountStatus.Locked else AccountStatus.Active,
  created = created,
  lastLogin = lastLogin,
  vip = vip,
)

internal fun GameTransactionResponse.toGameTransaction(): GameTransaction = GameTransaction(
  code = code,
  accountId = accountId,
  amount = amount,
  method = method,
  time = time,
  type = if (type == TRANSACTION_TYPE_DEDUCT) TransactionType.Deduct else TransactionType.Deposit,
)
