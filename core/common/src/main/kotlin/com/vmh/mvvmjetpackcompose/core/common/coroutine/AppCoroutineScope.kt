package com.vmh.mvvmjetpackcompose.core.common.coroutine

import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * A [CoroutineScope] that is tied to the lifecycle of a [android.app.Application].
 * Use this scope instead of [kotlinx.coroutines.GlobalScope].
 *
 * See [Avoid GlobalScope](https://developer.android.com/kotlin/coroutines/coroutines-best-practices#global-scope)
 * for more information.
 */
interface AppCoroutineScope : CoroutineScope

internal class DefaultAppCoroutineScope @Inject constructor(appCoroutineDispatchers: AppCoroutineDispatchers) :
  AppCoroutineScope {
  override val coroutineContext: CoroutineContext =
    appCoroutineDispatchers.io + SupervisorJob()

  override fun toString(): String = "DefaultAppCoroutineScope(coroutineContext=$coroutineContext)"
}
