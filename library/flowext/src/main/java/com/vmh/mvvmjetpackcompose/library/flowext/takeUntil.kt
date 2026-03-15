package com.vmh.mvvmjetpackcompose.library.flowext

import com.vmh.mvvmjetpackcompose.library.flowext.internal.ClosedException
import com.vmh.mvvmjetpackcompose.library.flowext.internal.checkOwnership
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

/**
 * Emits the values emitted by the source [Flow] until a [notifier] [Flow] emits a value or completes.
 *
 * @param notifier The [Flow] whose first emitted value or complete event
 * will cause the output [Flow] of [takeUntil] to stop emitting values from the source [Flow].
 */
public fun <T> Flow<T>.takeUntil(notifier: Flow<Any?>): Flow<T> = flow {
  val ownershipMarker = Any()

  try {
    coroutineScope {
      val job = launch(start = CoroutineStart.UNDISPATCHED) {
        notifier.take(1).collect()
        throw ClosedException(ownershipMarker)
      }

      collect { emit(it) }
      job.cancel()
    }
  } catch (e: ClosedException) {
    e.checkOwnership(owner = ownershipMarker)
  }
}
