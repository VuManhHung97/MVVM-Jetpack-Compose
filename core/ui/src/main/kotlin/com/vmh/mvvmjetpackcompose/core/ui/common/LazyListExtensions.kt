package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isScrolledToEnd(visibleThreshold: Int): Boolean {
  require(visibleThreshold >= 0) { "visibleThreshold must be non-negative" }
  val layoutInfo = this.layoutInfo
  val totalItemsCount = layoutInfo.totalItemsCount
  if (totalItemsCount == 0) return false
  val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return false
  val lastIndex = totalItemsCount - 1
  return lastVisibleItem.index + visibleThreshold >= lastIndex
}
