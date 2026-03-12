package com.vmh.mvvmjetpackcompose.library.flowext

import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

/**
 * Returns a [Flow] that skips items emitted by the source [Flow] until a second [Flow] emits a value or completes.
 *
 * @param notifier The second [Flow] that has to emit a value before the source [Flow]'s values
 * begin to be mirrored by the resulting [Flow].
 */
public fun <T> Flow<T>.skipUntil(notifier: Flow<Any?>): Flow<T> = flow {
  coroutineScope {
    val shouldEmit = AtomicBoolean(false)

    val job = launch(start = CoroutineStart.UNDISPATCHED) {
      notifier.take(1).collect()
      shouldEmit.set(true)
    }

    collect {
      if (shouldEmit.get()) {
        emit(it)
      }
    }

    job.cancel()
  }
}

/**
 * This function is an alias to [skipUntil] operator.
 *
 * @see skipUntil
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Flow<T>.dropUntil(notifier: Flow<Any?>): Flow<T> = skipUntil(notifier)
