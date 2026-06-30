package com.vmh.mvvmjetpackcompose

sealed interface MainSingleEvent {
  data object NavigateToAuthentication : MainSingleEvent

  data object NavigateToSearch : MainSingleEvent
}
