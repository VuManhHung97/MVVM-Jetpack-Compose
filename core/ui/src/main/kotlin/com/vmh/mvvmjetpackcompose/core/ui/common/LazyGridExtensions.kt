package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.compose.foundation.lazy.grid.LazyGridState

/**
 * Checks if the LazyGridState has been scrolled to the end of its content.
 *
 * @param visibleThreshold The number of items before the last item to consider as "end".
 *        Defaults to 0, meaning the last item must be fully visible.
 *        Must be non-negative.
 * @return True if the last visible item's index plus the threshold is at or beyond
 *         the total item count, false otherwise (including for empty grids).
 */
fun LazyGridState.isScrolledToEnd(visibleThreshold: Int): Boolean {
  require(visibleThreshold >= 0) { "visibleThreshold must be non-negative" }
  val layoutInfo = this.layoutInfo
  val totalItemsCount = layoutInfo.totalItemsCount

  if (totalItemsCount == 0) return false

  val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return false
  val lastIndex = totalItemsCount - 1
  return lastVisibleItem.index + visibleThreshold >= lastIndex
}
