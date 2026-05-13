package com.vmh.mvvmjetpackcompose.core.model.auth

sealed interface AuthenticationState {
  data class Authenticated(val user: User) : AuthenticationState
  data object Unauthenticated : AuthenticationState
}
