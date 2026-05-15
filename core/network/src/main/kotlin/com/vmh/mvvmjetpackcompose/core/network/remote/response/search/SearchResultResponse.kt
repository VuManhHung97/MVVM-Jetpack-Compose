package com.vmh.mvvmjetpackcompose.core.network.remote.response.search

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class SearchResultResponse(@param:Json(name = "id") val id: Long, @param:Json(name = "title") val title: String)
