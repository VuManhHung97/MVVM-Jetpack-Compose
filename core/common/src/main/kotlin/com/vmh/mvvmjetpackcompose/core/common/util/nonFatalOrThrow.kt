package com.vmh.mvvmjetpackcompose.core.common.util

import kotlinx.coroutines.CancellationException

/**
 * Returns the Throwable if NonFatal and throws it otherwise.
 *
 * @throws Throwable the Throwable `this` if Fatal
 * @return the Throwable `this` if NonFatal
 */
fun Throwable.nonFatalOrThrow(): Throwable = if (NonFatal(this)) this else throw this

@Suppress("FunctionName")
fun NonFatal(t: Throwable): Boolean = when (t) {
  is VirtualMachineError,
  is ThreadDeath,
  is InterruptedException,
  is LinkageError,
  is CancellationException,
  -> false

  else -> true
}
