package com.vmh.mvvmjetpackcompose.library.flowext

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Maps values in the [Flow] to [successful results][Result.success],
 * and catches and wraps any exception into a [failure result][Result.failure].
 */
@FlowExtPreview
public fun <T> Flow<T>.mapToResult(): Flow<Result<T>> = map { Result.success(it) }
  .catchAndReturn { Result.failure(it) }

/**
 * Maps a [Flow] of [Result]s to a [Flow] of a mapped [Result]s.
 *
 * Any exception thrown by the [transform] function is caught,
 * and emitted as a [failure result][Result.failure] to the resulting flow.
 *
 * @see Result.mapCatching
 */
@FlowExtPreview
public fun <T, R> Flow<Result<T>>.mapResultCatching(transform: suspend (T) -> R): Flow<Result<R>> = map { result ->
  result
    .mapCatching { transform(it) }
    .onFailure {
      if (it is CancellationException) {
        throw it
      }
    }
}

/**
 * Maps a [Flow] of [Result]s to a [Flow] of values from successful results.
 * Failure results are re-thrown as exceptions.
 *
 * @see Result.getOrThrow
 */
@FlowExtPreview
public fun <T> Flow<Result<T>>.throwFailure(): Flow<T> = map { it.getOrThrow() }
