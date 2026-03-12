package com.vmh.mvvmjetpackcompose.library.flowext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Adapt this [Flow] to be a `Flow<R>`.
 *
 * This Flow is wrapped as a `Flow<R>` which checks at run-time that each value event emitted
 * by this Flow is also an instance of R.
 *
 * At the collection time, if this [Flow] has any value that is not an instance of R,
 * a [ClassCastException] will be thrown.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <reified R> Flow<*>.cast(): Flow<R> = map { it as R }

/**
 * Adapt this `Flow<T?>` to be a `Flow<T>`.
 *
 * At the collection time, if this [Flow] has any `null` value,
 * a [NullPointerException] will be thrown.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <reified T : Any> Flow<T?>.castNotNull(): Flow<T> = map { it!! }

/**
 * Adapt this `Flow<T>` to be a `Flow<T?>`.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Flow<T>.castNullable(): Flow<T?> = this

/**
 * Adapt this `Flow<*>` to be a `Flow<R?>`.
 * At the collection time, if this [Flow] has any value that is not an instance of R,
 * `null` will be emitted.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <reified R> Flow<*>.safeCast(): Flow<R?> = map { it as? R }
