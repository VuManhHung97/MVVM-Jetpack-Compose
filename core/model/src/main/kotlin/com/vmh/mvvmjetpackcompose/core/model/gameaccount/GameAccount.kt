package com.vmh.mvvmjetpackcompose.core.model.gameaccount

/**
 * A player's game account managed from the Võ Lâm 2 admin app. Pure domain model.
 */
data class GameAccount(
  val id: String,
  val username: String,
  val character: String,
  val clan: String,
  val level: Int,
  val balance: Long,
  val status: AccountStatus,
  val created: String,
  val lastLogin: String,
  val vip: String,
)

enum class AccountStatus { Active, Locked }
