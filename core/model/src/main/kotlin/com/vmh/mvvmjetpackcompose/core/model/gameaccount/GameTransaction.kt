package com.vmh.mvvmjetpackcompose.core.model.gameaccount

/**
 * A single Xu (in-game currency) transaction against a [GameAccount]. Pure domain model.
 */
data class GameTransaction(
  val code: String,
  val accountId: String,
  val amount: Long,
  val method: String,
  val time: String,
  val type: TransactionType,
)

enum class TransactionType { Deposit, Deduct }
