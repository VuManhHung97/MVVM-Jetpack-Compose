@file:Suppress("NOTHING_TO_INLINE", "TooManyFunctions")

package com.vmh.mvvmjetpackcompose.library.flowext

import kotlin.time.Duration
import kotlinx.coroutines.delay as coroutinesDelay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

// ----------------------------------------------- REPEAT FOREVER -----------------------------------------------

/**
 * Returns a [Flow] that repeats all values emitted by the original [Flow] indefinitely.
 *
 * Note: If the source [Flow] is completed synchronously immediately (e.g. [emptyFlow]),
 * this will cause an infinite loop.
 */
public fun <T> Flow<T>.repeat(): Flow<T> = repeatInternal(
  flow = this,
  count = 0,
  infinite = true,
  delay = noDelay(),
)

/**
 * Returns a [Flow] that repeats all values emitted by the original [Flow] indefinitely,
 * with a delay computed by [delay] function between each repetition.
 *
 * Note: If the source [Flow] is completed synchronously immediately (e.g. [emptyFlow]),
 * and [delay] returns [Duration.ZERO] or a negative value,
 * this will cause an infinite loop.
 */
public fun <T> Flow<T>.repeat(delay: suspend (count: Int) -> Duration): Flow<T> = repeatInternal(
  flow = this,
  count = 0,
  infinite = true,
  delay = delaySelector(delay),
)

/**
 * Returns a [Flow] that repeats all values emitted by the original [Flow] indefinitely,
 * with a fixed [delay] between each repetition.
 *
 * Note: If the source [Flow] is completed synchronously immediately (e.g. [emptyFlow]),
 * and [delay] is [Duration.ZERO], this will cause an infinite loop.
 */
public fun <T> Flow<T>.repeat(delay: Duration): Flow<T> = repeatInternal(
  flow = this,
  count = 0,
  infinite = true,
  delay = fixedDelay(delay),
)

// --------------------------------------------------- REPEAT COUNT ---------------------------------------------------

/**
 * Returns a [Flow] that repeats all values emitted by the original [Flow] at most [count] times.
 * If [count] is zero or negative, the resulting [Flow] completes immediately without emitting any items (i.e. [emptyFlow]).
 */
public fun <T> Flow<T>.repeat(count: Int): Flow<T> = repeatInternal(
  flow = this,
  count = count,
  infinite = false,
  delay = noDelay(),
)

/**
 * Returns a [Flow] that repeats all values emitted by the original [Flow] at most [count] times,
 * with a delay computed by [delay] function between each repetition.
 *
 * If [count] is zero or negative, the resulting [Flow] completes immediately without emitting any items (i.e. [emptyFlow]).
 */
public fun <T> Flow<T>.repeat(count: Int, delay: suspend (count: Int) -> Duration): Flow<T> = repeatInternal(
  flow = this,
  count = count,
  infinite = false,
  delay = delaySelector(delay),
)

/**
 * Returns a [Flow] that repeats all values emitted by the original [Flow] indefinitely,
 * with a fixed [delay] between each repetition.
 *
 * If [count] is zero or negative, the resulting [Flow] completes immediately without emitting any items (i.e. [emptyFlow]).
 */
public fun <T> Flow<T>.repeat(count: Int, delay: Duration): Flow<T> = repeatInternal(
  flow = this,
  count = count,
  infinite = false,
  delay = fixedDelay(delay),
)

// ---------------------------------------------------- INTERNAL ----------------------------------------------------

private typealias DelayDurationSelector = suspend (count: Int) -> Duration

@Suppress("FunctionOnlyReturningConstant")
private inline fun noDelay(): DelayDurationSelector? = null

private inline fun fixedDelay(delay: Duration): DelayDurationSelector? = if (delay.isZeroOrNegative()) {
  noDelay()
} else {
  FixedDelayDurationSelector(delay)
}

private inline fun delaySelector(noinline delay: DelayDurationSelector): DelayDurationSelector = delay

private inline fun Duration.isZeroOrNegative() = this == Duration.ZERO || isNegative()

/**
 * Used when the delay duration is fixed.
 * This is an optimization to avoid integer overflow.
 */
private class FixedDelayDurationSelector(val duration: Duration) : DelayDurationSelector {
  override suspend fun invoke(count: Int): Duration = duration
}

private fun <T> repeatInternal(flow: Flow<T>, count: Int, infinite: Boolean, delay: DelayDurationSelector?): Flow<T> =
  when {
    infinite -> repeatIndefinitely(
      flow = flow,
      delay = delay,
    )

    count <= 0 -> emptyFlow()
    else -> repeatAtMostCount(
      flow = flow,
      count = count,
      delay = delay,
    )
  }

private fun <T> repeatIndefinitely(flow: Flow<T>, delay: DelayDurationSelector?): Flow<T> = when (delay) {
  null -> flow {
    while (true) {
      emitAll(flow)
    }
  }

  is FixedDelayDurationSelector -> flow {
    while (true) {
      emitAll(flow)
      coroutinesDelay(delay.duration)
    }
  }

  else -> flow {
    var soFar = 1

    while (true) {
      emitAll(flow)
      coroutinesDelay(delay(soFar++))
    }
  }
}

private fun <T> repeatAtMostCount(flow: Flow<T>, count: Int, delay: DelayDurationSelector?): Flow<T> = when (delay) {
  null -> flow {
    repeat(count) {
      emitAll(flow)
    }
  }

  is FixedDelayDurationSelector -> flow {
    repeat(count) {
      emitAll(flow)
      coroutinesDelay(delay.duration)
    }
  }

  else -> flow {
    for (soFar in 1..count) {
      emitAll(flow)
      coroutinesDelay(delay(soFar))
    }
  }
}
