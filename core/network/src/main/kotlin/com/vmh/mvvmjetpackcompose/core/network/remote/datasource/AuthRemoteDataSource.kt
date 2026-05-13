package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.response.user.UserResponse

interface AuthRemoteDataSource {
  suspend fun signUp(email: String, password: String): Result<Unit, AppError.ApiException>

  suspend fun signIn(email: String, password: String): Result<UserResponse, AppError.ApiException>
}
