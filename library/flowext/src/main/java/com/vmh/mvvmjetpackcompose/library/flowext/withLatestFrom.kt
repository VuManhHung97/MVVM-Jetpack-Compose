package com.vmh.mvvmjetpackcompose.library.flowext

import com.vmh.mvvmjetpackcompose.library.flowext.internal.INTERNAL_NULL_VALUE
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Merges two [Flow]s into one [Flow] by combining each value from self with the latest value from the second [Flow], if any.
 * Values emitted by self before the second [Flow] has emitted any values will be omitted.
 *
 * @param other Second [Flow]
 * @param transform A transform function to apply to each value from self combined with the latest value from the second [Flow], if any.
 */
public fun <A, B, R> Flow<A>.withLatestFrom(other: Flow<B>, transform: suspend (A, B) -> R): Flow<R> {
  return flow {
    val otherRef = AtomicReference<Any?>(null)

    try {
      coroutineScope {
        val otherCollectionJob = launch(start = CoroutineStart.UNDISPATCHED) {
          other.collect { otherRef.set(it ?: INTERNAL_NULL_VALUE) }
        }

        collect { value ->
          emit(
            transform(
              value,
              INTERNAL_NULL_VALUE.unbox(otherRef.get() ?: return@collect),
            ),
          )
        }
        otherCollectionJob.cancelAndJoin()
      }
    } finally {
      otherRef.lazySet(null) // Clear the reference to avoid memory leaks
    }
  }
}

@Suppress("NOTHING_TO_INLINE")
public inline fun <A, B> Flow<A>.withLatestFrom(other: Flow<B>): Flow<Pair<A, B>> = withLatestFrom(other, ::Pair)
