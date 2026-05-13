package com.vmh.mvvmjetpackcompose.core.common.coroutine

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * An interface that provides properties for accessing commonly used [CoroutineDispatcher]s. This differs from the
 * [Dispatchers] object in that it is an interface, it can easily be mocked and tested,
 * and different implementations can easily be made to adapt to
 * different scenarios.
 *
 * See [Inject Dispatchers](https://developer.android.com/kotlin/coroutines/coroutines-best-practices#inject-dispatchers)
 * for more information.
 */
interface AppCoroutineDispatchers {
  val main: CoroutineDispatcher
  val immediateMain: CoroutineDispatcher
  val io: CoroutineDispatcher
  val default: CoroutineDispatcher
  val unconfined: CoroutineDispatcher
}

@Suppress("UseDataClass") // Not a pure data holder class.
internal class DefaultAppCoroutineDispatchers @Inject constructor() : AppCoroutineDispatchers {
  override val main: CoroutineDispatcher get() = Dispatchers.Main
  override val immediateMain: CoroutineDispatcher get() = Dispatchers.Main.immediate
  override val io: CoroutineDispatcher get() = Dispatchers.IO
  override val default: CoroutineDispatcher get() = Dispatchers.Default
  override val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
}
