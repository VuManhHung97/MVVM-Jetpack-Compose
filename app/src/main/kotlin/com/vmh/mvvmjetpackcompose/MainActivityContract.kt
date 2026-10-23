package com.vmh.mvvmjetpackcompose

sealed interface MainSingleEvent {
  data object NavigateToAuthentication : MainSingleEvent

  data class NavigateToSearch(val keyword: String?) : MainSingleEvent
}
