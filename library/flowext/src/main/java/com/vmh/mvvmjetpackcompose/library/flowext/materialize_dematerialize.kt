package com.vmh.mvvmjetpackcompose.library.flowext

import com.vmh.mvvmjetpackcompose.library.flowext.internal.ClosedException
import com.vmh.mvvmjetpackcompose.library.flowext.internal.checkOwnership
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

/**
 * Represents all of the notifications from the source [Flow] as `value` emissions marked with their original types within [Event] objects.
 */
public fun <T> Flow<T>.materialize(): Flow<Event<T>> = map<T, Event<T>> { Event.Value(it) }
  .onCompletion { if (it === null) emit(Event.Complete) }
  .catch { emit(Event.Error(it)) }

/**
 * Converts a [Flow] of [Event] objects into the emissions that they represent.
 */
public fun <T> Flow<Event<T>>.dematerialize(): Flow<T> = flow {
  val ownershipMarker = Any()

  try {
    collect {
      when (it) {
        Event.Complete -> throw ClosedException(ownershipMarker)
        is Event.Error -> throw it.error
        is Event.Value -> emit(it.value)
      }
    }
  } catch (e: ClosedException) {
    e.checkOwnership(owner = ownershipMarker)
  }
}
