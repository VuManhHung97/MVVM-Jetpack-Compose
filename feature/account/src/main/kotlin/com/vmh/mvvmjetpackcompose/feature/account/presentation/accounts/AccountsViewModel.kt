package com.vmh.mvvmjetpackcompose.feature.account.presentation.accounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vmh.mvvmjetpackcompose.core.common.extension.mapToPersistentList
import com.vmh.mvvmjetpackcompose.core.domain.repository.GameAccountRepository
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.GameAccount
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.DepositUiState
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.LockUiState
import com.vmh.mvvmjetpackcompose.lifecycle.EventChannel
import com.vmh.mvvmjetpackcompose.lifecycle.HasEventFlow
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.formatXu
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(FlowPreview::class)
@Suppress("TooManyFunctions")
@HiltViewModel
internal class AccountsViewModel @Inject constructor(
  private val gameAccountRepository: GameAccountRepository,
  private val eventChannel: EventChannel<AccountsSingleEvent>,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel(eventChannel),
  HasEventFlow<AccountsSingleEvent> by eventChannel {

  private val _uiStateFlow = MutableStateFlow(AccountsUiState.initial)
  val uiStateFlow: StateFlow<AccountsUiState> = _uiStateFlow.asStateFlow()

  /** Raw keyword bound to the search field; debounced separately for filtering. */
  val queryStateFlow: StateFlow<String> = savedStateHandle.getStateFlow(QUERY_KEY, "")

  private val statusFilterFlow = MutableStateFlow(AccountStatusFilter.All)

  private inline fun emitState(f: (AccountsUiState) -> AccountsUiState) = _uiStateFlow.update(f)

  init {
    observeAccounts()
  }

  private fun observeAccounts() {
    viewModelScope.launch {
      combine(
        gameAccountRepository.observeAccounts(),
        queryStateFlow.debounce(SEARCH_DEBOUNCE_DURATION).map { it.trim().lowercase(VIETNAM) }.distinctUntilChanged(),
        statusFilterFlow,
      ) { accounts, normalizedQuery, filter ->
        accounts.filterBy(normalizedQuery = normalizedQuery, filter = filter)
      }
        .catch { throwable ->
          Timber.e(throwable, "AccountsViewModel observe failure")
          emitState { AccountsUiState.Error(error = AppError.UnknownException(throwable)) }
        }
        .collect { filteredAccounts ->
          emitState { current ->
            AccountsUiState.Content(
              selectedFilter = statusFilterFlow.value,
              accountCount = filteredAccounts.size,
              accounts = filteredAccounts.mapToPersistentList { it.toAccountUiItem() },
              deposit = (current as? AccountsUiState.Content)?.deposit,
              lock = (current as? AccountsUiState.Content)?.lock,
            )
          }
        }
    }
  }

  private fun List<GameAccount>.filterBy(normalizedQuery: String, filter: AccountStatusFilter): List<GameAccount> =
    filter {
      val matchesQuery = normalizedQuery.isEmpty() ||
        it.username.lowercase(VIETNAM).contains(normalizedQuery) ||
        it.character.lowercase(VIETNAM).contains(normalizedQuery)
      val matchesStatus = filter.status == null || it.status == filter.status
      matchesQuery && matchesStatus
    }

  internal fun onQueryChange(query: String) {
    savedStateHandle[QUERY_KEY] = query
  }

  internal fun onQueryClear() {
    savedStateHandle[QUERY_KEY] = ""
  }

  internal fun onStatusFilterSelect(filter: AccountStatusFilter) = statusFilterFlow.update { filter }

  // ---------------------------------------- Deposit ----------------------------------------

  internal fun onDepositOpen(accountId: String, username: String, balanceFormatted: String) = emitState { state ->
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
    val deposit = (_uiStateFlow.value as? AccountsUiState.Content)?.deposit ?: return
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
        AccountsSingleEvent.DepositSuccess(amountFormatted = formatXu(amount), username = deposit.username),
      )
    }
  }

  // ---------------------------------------- Lock / unlock ----------------------------------------

  internal fun onLockOpen(accountId: String, username: String, isCurrentlyActive: Boolean) = emitState { state ->
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
    val lock = (_uiStateFlow.value as? AccountsUiState.Content)?.lock ?: return
    viewModelScope.launch {
      gameAccountRepository.setLocked(
        accountId = lock.accountId,
        isLocked = lock.isLocking,
        reason = lock.reason.ifBlank { null },
      )
      emitState { state -> state.updateContent { it.copy(lock = null) } }
      eventChannel.send(AccountsSingleEvent.LockChanged(isLocked = lock.isLocking, username = lock.username))
    }
  }

  private companion object {
    const val QUERY_KEY = "AccountsViewModel#query"
    val SEARCH_DEBOUNCE_DURATION = 300.milliseconds
    val VIETNAM: Locale = Locale.forLanguageTag("vi-VN")
  }
}

private inline fun AccountsUiState.updateContent(
  transform: (AccountsUiState.Content) -> AccountsUiState.Content,
): AccountsUiState = if (this is AccountsUiState.Content) transform(this) else this
