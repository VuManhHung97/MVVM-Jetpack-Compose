@file:Suppress("MaxLineLength")

package com.vmh.mvvmjetpackcompose.library.flowext

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * Creates a _cold_ flow that produces a single value from the given [function].
 * It calls [function] for each new [FlowCollector].
 *
 * This function is similar to [RxJava's fromCallable](http://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Flowable.html#fromCallable-java.util.concurrent.Callable-).
 * See also [flowFromSuspend] for the suspend version.
 *
 * The returned [Flow] is cancellable and has the same behaviour as [kotlinx.coroutines.flow.cancellable].
 *
 * ## Example of usage:
 *
 * ```kotlin
 * fun call(): R = ...
 * fun callAsFlow(): Flow<R> = flowFromNonSuspend(::call)
 * ```
 * ## Another example:
 *
 * ```kotlin
 * var count = 0L
 * val flow = flowFromNonSuspend { count++ }
 *
 * flow.collect { println("flowFromNonSuspend: $it") }
 * println("---")
 * flow.collect { println("flowFromNonSuspend: $it") }
 * println("---")
 * flow.collect { println("flowFromNonSuspend: $it") }
 * ```
 *
 * Output:
 *
 * ```none
 * flowFromNonSuspend: 0
 * ---
 * flowFromNonSuspend: 1
 * ---
 * flowFromNonSuspend: 2
 * ```
 *
 * @see flowFromSuspend
 */
public fun <T> flowFromNonSuspend(function: () -> T): Flow<T> = FlowFromNonSuspend(function)

// We don't need to use `AbstractFlow` here because we only emit a single value without a context switch,
// and we guarantee all Flow's constraints: context preservation and exception transparency.
private class FlowFromNonSuspend<T>(private val function: () -> T) : Flow<T> {
  override suspend fun collect(collector: FlowCollector<T>) {
    val value = function()
    currentCoroutineContext().ensureActive()
    collector.emit(value)
  }
}
