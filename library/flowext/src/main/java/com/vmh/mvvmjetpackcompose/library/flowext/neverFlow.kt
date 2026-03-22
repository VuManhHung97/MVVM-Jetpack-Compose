package com.vmh.mvvmjetpackcompose.library.flowext

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * A [Flow] that never emits any values to the [FlowCollector] and never completes.
 */
public sealed interface NeverFlow : Flow<Nothing> {
  override suspend fun collect(collector: FlowCollector<Nothing>): Nothing

  public companion object : NeverFlow {
    override suspend fun collect(collector: FlowCollector<Nothing>): Nothing = awaitCancellation()
  }
}

/**
 * Returns a [NeverFlow] that never emits any values to the [FlowCollector] and never completes.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun neverFlow(): NeverFlow = NeverFlow
