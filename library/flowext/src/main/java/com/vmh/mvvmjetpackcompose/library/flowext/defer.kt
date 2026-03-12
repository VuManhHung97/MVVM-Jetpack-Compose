package com.vmh.mvvmjetpackcompose.library.flowext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Creates a [Flow] that, on collection, calls a [Flow] factory to make a [Flow] for each new [FlowCollector].
 *
 * In some circumstances, waiting until the last minute (that is, until collection time)
 * to generate the [Flow] can ensure that collectors receive the freshest data.
 *
 * Example of usage:
 *
 * ```
 * suspend fun remoteCall1(): R1 = ...
 * suspend fun remoteCall2(r1: R1): R2 = ...
 *
 * fun example1(): Flow<R2> = defer {
 *   val r1 = remoteCall1()
 *   val r2 = remoteCall2(r1)
 *   flowOf(r2)
 * }
 *
 * fun example2(): Flow<R1> = defer { flowOf(remoteCall1()) }
 * ```
 */
public fun <T> defer(flowFactory: suspend () -> Flow<T>): Flow<T> = flow { emitAll(flowFactory()) }
