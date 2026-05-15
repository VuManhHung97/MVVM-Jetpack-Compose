package com.vmh.mvvmjetpackcompose.core.network.remote.service

import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CHECK_ACCESS_TOKEN
import com.vmh.mvvmjetpackcompose.core.network.remote.interceptor.ApiConstants.Headers.CUSTOM_HEADER
import com.vmh.mvvmjetpackcompose.core.network.remote.response.BaseResponse
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

internal interface SearchApiService {

  @Headers("$CUSTOM_HEADER: $CHECK_ACCESS_TOKEN")
  @GET("v1/api/search/suggestions")
  suspend fun getSearchAutocomplete(
    @Query("keyword") keyword: String,
    @Query("limit") limit: Int,
  ): BaseResponse.Data<List<String>>

  @Headers("$CUSTOM_HEADER: $CHECK_ACCESS_TOKEN")
  @GET("v1/api/search/history")
  suspend fun getSearchHistory(
    @Query("keyword") keyword: String,
    @Query("limit") limit: Int,
  ): BaseResponse.Data<List<String>>

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): SearchApiService = retrofit.create()
  }
}
