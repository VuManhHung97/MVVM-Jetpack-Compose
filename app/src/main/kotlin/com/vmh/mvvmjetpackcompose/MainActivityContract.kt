package com.vmh.mvvmjetpackcompose

sealed interface MainSingleEvent {
  data object NavigateToAuthentication : MainSingleEvent
}
