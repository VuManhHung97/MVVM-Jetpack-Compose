package com.vmh.mvvmjetpackcompose.library.flowext

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.yield

//
// race / raceWith
//

/**
 * Mirrors the one [Flow] in an array of several [Flow]s that first either emits a value
 * or sends a termination event (error or complete event).
 *
 * @see race
 */
@ExperimentalCoroutinesApi
public fun <T> race(flow1: Flow<T>, flow2: Flow<T>, vararg flows: Flow<T>): Flow<T> = race(
  buildList(capacity = 2 + flows.size) {
    add(flow1)
    add(flow2)
    addAll(flows)
  },
)

/**
 * Mirrors the current [Flow] or the other [Flow]s provided of which the first either emits a value
 * or sends a termination event (error or complete event).
 *
 * @see race
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<T>.raceWith(flow: Flow<T>, vararg flows: Flow<T>): Flow<T> = race(
  buildList(capacity = 2 + flows.size) {
    add(this@raceWith)
    add(flow)
    addAll(flows)
  },
)

/**
 * Mirrors the one [Flow] in an [Iterable] of several [Flow]s that first either emits a value
 * or sends a termination event (error or complete event).
 *
 * When you pass a number of source [Flow]s to [race], it will pass through the emissions
 * and events of exactly one of these [Flow]s: the first one that sends an event to [race],
 * either by emitting a value or sending an error or complete event.
 * [race] will cancel the emissions and events of all of the other source [Flow]s.
 */
@ExperimentalCoroutinesApi
public fun <T> race(flows: Iterable<Flow<T>>): Flow<T> = flow {
  coroutineScope {
    val channels = flows.map { flow ->
      // Produce the values using the default (rendezvous) channel
      produce {
        flow.collect {
          send(it)
          yield() // Emulate fairness, giving each flow chance to emit
        }
      }
    }

    if (channels.isEmpty()) {
      return@coroutineScope
    }
    channels
      .singleOrNull()
      ?.let { return@coroutineScope emitAll(it) }

    val (winnerIndex, winnerResult) = select {
      channels.forEachIndexed { index, channel ->
        channel.onReceiveCatching {
          index to it
        }
      }
    }

    channels.forEachIndexed { index, channel ->
      if (index != winnerIndex) {
        channel.cancel()
      }
    }

    winnerResult
      .onSuccess {
        emit(it)
        emitAll(channels[winnerIndex])
      }
      .onFailure {
        it?.let { throw it }
      }
  }
}

//
// amb / ambWith
//

/**
 * This function is an alias to [race] operator.
 *
 * @see race
 */
@ExperimentalCoroutinesApi
public fun <T> amb(flow1: Flow<T>, flow2: Flow<T>, vararg flows: Flow<T>): Flow<T> = race(
  buildList(capacity = 2 + flows.size) {
    add(flow1)
    add(flow2)
    addAll(flows)
  },
)

/**
 * This function is an alias to [raceWith] operator.
 *
 * @see raceWith
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<T>.ambWith(flow: Flow<T>, vararg flows: Flow<T>): Flow<T> = race(
  buildList(capacity = 2 + flows.size) {
    add(this@ambWith)
    add(flow)
    addAll(flows)
  },
)

/**
 * This function is an alias to [race] operator.
 *
 * @see race
 */
@ExperimentalCoroutinesApi
public fun <T> amb(flows: Iterable<Flow<T>>): Flow<T> = race(flows)
