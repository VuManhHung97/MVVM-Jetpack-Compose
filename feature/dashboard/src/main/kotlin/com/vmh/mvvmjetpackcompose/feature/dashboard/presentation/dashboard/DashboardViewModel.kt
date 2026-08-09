package com.vmh.mvvmjetpackcompose.feature.dashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmh.mvvmjetpackcompose.core.common.extension.mapToPersistentList
import com.vmh.mvvmjetpackcompose.core.domain.repository.GameAccountRepository
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
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

private const val RECENT_TRANSACTION_COUNT = 4

@HiltViewModel
internal class DashboardViewModel @Inject constructor(private val gameAccountRepository: GameAccountRepository) :
  ViewModel() {

  private val _uiStateFlow = MutableStateFlow(DashboardUiState.initial)
  val uiStateFlow: StateFlow<DashboardUiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (DashboardUiState) -> DashboardUiState) = _uiStateFlow.update(f)

  init {
    observeRecentTransactions()
  }

  private fun observeRecentTransactions() {
    viewModelScope.launch {
      combine(
        gameAccountRepository.observeAccounts(),
        gameAccountRepository.observeTransactions(),
      ) { accounts, transactions ->
        transactions
          .take(RECENT_TRANSACTION_COUNT)
          .mapToPersistentList { it.toRecentTransactionUiItem(accounts) }
      }
        .catch { throwable ->
          Timber.e(throwable, "DashboardViewModel observe failure")
          emitState { DashboardUiState.Error(error = AppError.UnknownException(throwable)) }
        }
        .collect { recentTransactions ->
          emitState { DashboardUiState.Content(recentTransactions = recentTransactions) }
        }
    }
  }
}
