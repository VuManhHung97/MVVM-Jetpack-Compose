package com.vmh.mvvmjetpackcompose.feature.transaction.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmh.mvvmjetpackcompose.core.common.extension.mapToPersistentList
import com.vmh.mvvmjetpackcompose.core.domain.repository.GameAccountRepository
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.TransactionType
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.formatXu
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
internal class TransactionViewModel @Inject constructor(private val gameAccountRepository: GameAccountRepository) :
  ViewModel() {

  private val _uiStateFlow = MutableStateFlow(TransactionUiState.initial)
  val uiStateFlow: StateFlow<TransactionUiState> = _uiStateFlow.asStateFlow()

  private val selectedFilterFlow = MutableStateFlow(HistoryFilter.All)

  private inline fun emitState(f: (TransactionUiState) -> TransactionUiState) = _uiStateFlow.update(f)

  init {
    observeTransactions()
  }

  private fun observeTransactions() {
    viewModelScope.launch {
      combine(
        gameAccountRepository.observeAccounts(),
        gameAccountRepository.observeTransactions(),
        selectedFilterFlow,
      ) { accounts, transactions, filter ->
        val filtered = filter.type?.let { type -> transactions.filter { it.type == type } } ?: transactions
        TransactionUiState.Content(
          totalDepositFormatted = formatXu(
            transactions.filter {
              it.type == TransactionType.Deposit
            }.sumOf { it.amount },
          ),
          totalDeductFormatted = formatXu(
            transactions.filter {
              it.type == TransactionType.Deduct
            }.sumOf { it.amount },
          ),
          selectedFilter = filter,
          transactions = filtered.mapToPersistentList { it.toTransactionUiItem(accounts) },
        )
      }
        .catch { throwable ->
          Timber.e(throwable, "TransactionViewModel observe failure")
          emitState { TransactionUiState.Error(error = AppError.UnknownException(throwable)) }
        }
        .collect { content -> emitState { content } }
    }
  }

  internal fun onFilterSelect(filter: HistoryFilter) = selectedFilterFlow.update { filter }
}
