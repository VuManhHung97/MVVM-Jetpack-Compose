package com.vmh.mvvmjetpackcompose.library.flowext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll

/**
 * Catches exceptions in the flow completion and emits a single [item], then completes normally.
 */
@FlowExtPreview
public fun <T> Flow<T>.catchAndReturn(item: T): Flow<T> = catch { emit(item) }

/**
 * Catches exceptions in the flow completion and emits a single [item] provided by [itemSupplier],
 * then completes normally.
 */
@FlowExtPreview
public fun <T> Flow<T>.catchAndReturn(itemSupplier: suspend (cause: Throwable) -> T): Flow<T> =
  catch { cause -> emit(itemSupplier(cause)) }

/**
 * Catches exceptions in the flow completion and emits all the items from the [fallback] flow.
 * If the [fallback] flow also throws an exception, the exception is not caught and is rethrown.
 */
@FlowExtPreview
public fun <T> Flow<T>.catchAndResume(fallback: Flow<T>): Flow<T> = catch { emitAll(fallback) }

/**
 * Catches exceptions in the flow completion and emits all the items provided by [fallbackSupplier].
 * If the fallback flow also throws an exception, the exception is not caught and is rethrown.
 */
@FlowExtPreview
public fun <T> Flow<T>.catchAndResume(fallbackSupplier: suspend (cause: Throwable) -> Flow<T>): Flow<T> =
  catch { cause -> emitAll(fallbackSupplier(cause)) }
