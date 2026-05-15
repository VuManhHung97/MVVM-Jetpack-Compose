package com.vmh.mvvmjetpackcompose.core.data.mapper.search

import com.vmh.mvvmjetpackcompose.core.model.search.SearchResult
import com.vmh.mvvmjetpackcompose.core.network.remote.response.search.SearchResultResponse

internal fun SearchResultResponse.toSearchResult(): SearchResult = SearchResult(
  id = id,
  title = title,
)
