package com.vmh.mvvmjetpackcompose.feature.account.presentation.accountDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmh.mvvmjetpackcompose.core.common.extension.mapToPersistentList
import com.vmh.mvvmjetpackcompose.core.domain.repository.GameAccountRepository
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.DepositUiState
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.LockUiState
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.navigation.ACCOUNT_ID_ARG
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
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

@Suppress("TooManyFunctions")
@HiltViewModel
internal class AccountDetailViewModel @Inject constructor(
  private val gameAccountRepository: GameAccountRepository,
  private val eventChannel: EventChannel<AccountDetailSingleEvent>,
  savedStateHandle: SavedStateHandle,
) : ViewModel(eventChannel),
  HasEventFlow<AccountDetailSingleEvent> by eventChannel {

  private val accountId: String = checkNotNull(savedStateHandle[ACCOUNT_ID_ARG])

  private val _uiStateFlow = MutableStateFlow(AccountDetailUiState.initial)
  val uiStateFlow: StateFlow<AccountDetailUiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (AccountDetailUiState) -> AccountDetailUiState) = _uiStateFlow.update(f)

  init {
    observeAccount()
  }

  private fun observeAccount() {
    viewModelScope.launch {
      combine(
        gameAccountRepository.observeAccounts(),
        gameAccountRepository.observeTransactions(),
      ) { accounts, transactions ->
        val account = accounts.firstOrNull { it.id == accountId } ?: return@combine null
        val accountTransactions = transactions
          .filter { it.accountId == accountId }
          .mapToPersistentList { it.toDetailTransactionUiItem() }
        account to accountTransactions
      }
        .catch { throwable ->
          Timber.e(throwable, "AccountDetailViewModel observe failure")
          emitState { AccountDetailUiState.Error(error = AppError.UnknownException(throwable)) }
        }
        .collect { accountWithTransactions ->
          accountWithTransactions ?: return@collect
          val (account, transactions) = accountWithTransactions
          emitState { current ->
            account.toDetailContent(
              transactions = transactions,
              deposit = (current as? AccountDetailUiState.Content)?.deposit,
              lock = (current as? AccountDetailUiState.Content)?.lock,
            )
          }
        }
    }
  }

  // ---------------------------------------- Deposit ----------------------------------------

  internal fun onDepositOpen(username: String, balanceFormatted: String) = emitState { state ->
    state.updateContent {
      it.copy(
        deposit = DepositUiState(
          accountId = accountId,
          username = username,
          balanceFormatted = balanceFormatted,
          amount = "",
          note = "",
        ),
      )
    }
  }

  internal fun onDepositClose() = emitState { state -> state.updateContent { it.copy(deposit = null) } }

  internal fun onDepositAmountChange(amount: String) = emitState { state ->
    state.updateContent { content ->
      content.copy(deposit = content.deposit?.copy(amount = amount.filter { it.isDigit() }))
    }
  }

  internal fun onDepositQuickSelect(amount: Long) = emitState { state ->
    state.updateContent { it.copy(deposit = it.deposit?.copy(amount = amount.toString())) }
  }

  internal fun onDepositNoteChange(note: String) = emitState { state ->
    state.updateContent { it.copy(deposit = it.deposit?.copy(note = note)) }
  }

  internal fun onDepositConfirm() {
    val deposit = (_uiStateFlow.value as? AccountDetailUiState.Content)?.deposit ?: return
    val amount = deposit.amountValue
    if (amount <= 0L) return
    viewModelScope.launch {
      gameAccountRepository.deposit(
        accountId = deposit.accountId,
        amount = amount,
        note = deposit.note.ifBlank {
          null
        },
      )
      emitState { state -> state.updateContent { it.copy(deposit = null) } }
      eventChannel.send(
        AccountDetailSingleEvent.DepositSuccess(amountFormatted = formatXu(amount), username = deposit.username),
      )
    }
  }

  // ---------------------------------------- Lock / unlock ----------------------------------------

  internal fun onLockOpen(username: String, isCurrentlyActive: Boolean) = emitState { state ->
    state.updateContent {
      it.copy(
        lock = LockUiState(accountId = accountId, username = username, isLocking = isCurrentlyActive, reason = ""),
      )
    }
  }

  internal fun onLockClose() = emitState { state -> state.updateContent { it.copy(lock = null) } }

  internal fun onLockReasonChange(reason: String) = emitState { state ->
    state.updateContent { it.copy(lock = it.lock?.copy(reason = reason)) }
  }

  internal fun onLockConfirm() {
    val lock = (_uiStateFlow.value as? AccountDetailUiState.Content)?.lock ?: return
    viewModelScope.launch {
      gameAccountRepository.setLocked(
        accountId = lock.accountId,
        isLocked = lock.isLocking,
        reason = lock.reason.ifBlank { null },
      )
      emitState { state -> state.updateContent { it.copy(lock = null) } }
      eventChannel.send(AccountDetailSingleEvent.LockChanged(isLocked = lock.isLocking, username = lock.username))
    }
  }
}

private inline fun AccountDetailUiState.updateContent(
  transform: (AccountDetailUiState.Content) -> AccountDetailUiState.Content,
): AccountDetailUiState = if (this is AccountDetailUiState.Content) transform(this) else this
