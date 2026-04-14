package com.vmh.mvvmjetpackcompose.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun <T> rememberFlowWithLifecycle(
  flow: Flow<T>,
  lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
  minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
): Flow<T> = remember(flow, lifecycleOwner, minActiveState) {
  flow.flowWithLifecycle(
    lifecycle = lifecycleOwner.lifecycle,
    minActiveState = minActiveState,
  )
}

@Suppress("ComposableNaming")
@Composable
fun <T> Flow<T>.collectInLaunchedEffectWithLifecycle(
  vararg keys: Any?,
  lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
  minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
  collector: suspend CoroutineScope.(T) -> Unit,
) {
  val flow = this
  val currentCollector by rememberUpdatedState(collector)

  LaunchedEffect(flow, lifecycleOwner, minActiveState, *keys) {
    withContext(Dispatchers.Main.immediate) {
      lifecycleOwner.repeatOnLifecycle(minActiveState) {
        flow.collect { currentCollector(it) }
      }
    }
  }
}
