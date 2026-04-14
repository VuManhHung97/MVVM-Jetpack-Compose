package com.vmh.mvvmjetpackcompose.lifecycle

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * repeatOnLifecycle launches the block in a new coroutine every time the
 * lifecycle is in and out of `minActiveState` lifecycle state.
 * @param owner The [LifecycleOwner] where the restarting collecting from `this` flow work will be
 * kept alive.
 * @param minActiveState [Lifecycle.State] in which the upstream flow gets collected. The
 * collection will stop if the lifecycle falls below that state, and will restart if it's in that
 * state again.
 */
inline fun <T> Flow<T>.collectIn(
  owner: LifecycleOwner,
  minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
  crossinline action: suspend (value: T) -> Unit,
): Job {
  check(minActiveState === Lifecycle.State.STARTED || minActiveState === Lifecycle.State.RESUMED) {
    "minActiveState must be STARTED or RESUMED"
  }
  return owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(state = minActiveState) { collect { action(it) } }
  }
}

/**
 * Launches a new coroutine and repeats `block` every time the Fragment's viewLifecycleOwner
 * is in and out of `minActiveState` lifecycle state.
 */
inline fun <T> Flow<T>.collectInViewLifecycle(
  fragment: Fragment,
  minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
  crossinline action: suspend (value: T) -> Unit,
): Job = collectIn(
  owner = fragment.viewLifecycleOwner,
  minActiveState = minActiveState,
  action = action,
)
