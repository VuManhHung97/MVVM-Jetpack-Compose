package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.response.user.ProfileResponse

interface UserRemoteDataSource {
  suspend fun getProfile(): Result<ProfileResponse, AppError.ApiException>
}
