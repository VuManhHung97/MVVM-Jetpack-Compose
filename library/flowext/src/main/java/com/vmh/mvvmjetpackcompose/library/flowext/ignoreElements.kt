package com.vmh.mvvmjetpackcompose.library.flowext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect

/**
 * Ignores all elements emitted by the source [Flow], only passes calls of `complete` or `error`.
 *
 * The returned flow does not emit any values. It completes normally when the source flow completes normally.
 * Otherwise, it completes with the same exception as the source flow.
 */
public fun <T> Flow<T>.ignoreElements(): Flow<Nothing> = IgnoreElementsFlow(this)

// We don't need to use `AbstractFlow` here because we don't emit any value.
private class IgnoreElementsFlow<T>(private val flow: Flow<T>) : Flow<Nothing> {
  override suspend fun collect(collector: FlowCollector<Nothing>) = flow.collect()
}
