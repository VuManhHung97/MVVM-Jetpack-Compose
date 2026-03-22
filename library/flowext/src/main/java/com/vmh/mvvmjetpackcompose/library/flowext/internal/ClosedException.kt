package com.vmh.mvvmjetpackcompose.library.flowext.internal

// Reference: https://github.com/Kotlin/kotlinx.coroutines/blob/8c516f5ab1fcc39629d2838489598135eedd7b80/kotlinx-coroutines-core/common/src/flow/internal/FlowExceptions.common.kt
// Change: We don't inherit from `kotlinx.coroutines.CancellationException`.

/**
 * This exception is thrown when an operator needs no more elements from the flow.
 *
 * The operator should never allow this exception to be thrown past its own boundary.
 * This exception can be safely ignored by non-terminal flow operator
 * if and only if it was caught by its owner (see usages of [checkOwnership]).
 *
 * Therefore, the [owner] parameter must be unique for every invocation of every operator.
 */
internal class ClosedException(@JvmField @Transient val owner: Any) :
  Exception("Flow was aborted, no more elements needed")

internal fun ClosedException.checkOwnership(owner: Any) {
  if (this.owner !== owner) throw this
}
