package com.vmh.mvvmjetpackcompose.core.network.remote.service

import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CHECK_ACCESS_TOKEN
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CUSTOM_HEADER
import com.vmh.mvvmjetpackcompose.core.network.remote.request.DepositRequestBody
import com.vmh.mvvmjetpackcompose.core.network.remote.request.LockAccountRequestBody
import com.vmh.mvvmjetpackcompose.core.network.remote.response.BaseResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount.GameAccountResponse
import com.vmh.mvvmjetpackcompose.core.network.remote.response.gameaccount.GameTransactionResponse
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Real API contract for game-account administration. Currently backed by a fake data source that
 * serves dummy JSON; switching to the real backend only requires binding the real implementation.
 */
internal interface GameAccountApiService {

  @Headers("$CUSTOM_HEADER: $CHECK_ACCESS_TOKEN")
  @GET("v1/api/game-accounts")
  suspend fun getGameAccounts(): BaseResponse.Data<List<GameAccountResponse>>

  @Headers("$CUSTOM_HEADER: $CHECK_ACCESS_TOKEN")
  @GET("v1/api/game-transactions")
  suspend fun getGameTransactions(): BaseResponse.Data<List<GameTransactionResponse>>

  @Headers("$CUSTOM_HEADER: $CHECK_ACCESS_TOKEN")
  @POST("v1/api/game-accounts/{accountId}/deposit")
  suspend fun deposit(@Path("accountId") accountId: String, @Body body: DepositRequestBody)

  @Headers("$CUSTOM_HEADER: $CHECK_ACCESS_TOKEN")
  @POST("v1/api/game-accounts/{accountId}/lock")
  suspend fun setLocked(@Path("accountId") accountId: String, @Body body: LockAccountRequestBody)

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): GameAccountApiService = retrofit.create()
  }
}
