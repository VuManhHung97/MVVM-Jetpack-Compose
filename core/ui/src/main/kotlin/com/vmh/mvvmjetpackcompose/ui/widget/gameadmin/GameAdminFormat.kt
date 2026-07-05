package com.vmh.mvvmjetpackcompose.ui.widget.gameadmin

import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameTransaction
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.TransactionType
import java.util.Locale

private val vietnam: Locale = Locale.forLanguageTag("vi-VN")

/** Group a Xu amount with Vietnamese thousands separators, e.g. 1250000 -> "1.250.000". */
fun formatXu(amount: Long): String = String.format(vietnam, "%,d", amount)

/** Signed amount string for a transaction, e.g. "+500.000" / "−100.000". */
fun GameTransaction.signedAmount(): String = (if (type == TransactionType.Deposit) "+" else "−") + formatXu(amount)

/** First letter of a username, uppercased for the initial badge. */
fun initialOf(username: String): String = username.firstOrNull()?.uppercase(vietnam) ?: "?"
