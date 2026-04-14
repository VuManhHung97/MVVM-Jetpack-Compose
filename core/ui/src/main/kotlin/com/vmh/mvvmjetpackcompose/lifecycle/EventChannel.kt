package com.vmh.mvvmjetpackcompose.lifecycle

import androidx.annotation.MainThread
import java.io.Closeable
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface HasEventFlow<E> {
  val eventFlow: Flow<E>
}

@MainThread
class EventChannel<E> @Inject constructor() :
  Closeable,
  HasEventFlow<E> {
  private val channel = Channel<E>(Channel.UNLIMITED)

  override val eventFlow: Flow<E> = channel.receiveAsFlow()

  /**
   * Must be called in Dispatchers.Main.immediate, otherwise it will throw an exception.
   * If you want to send an event from other Dispatcher,
   * use `withContext(Dispatchers.Main.immediate) { eventChannel.send(event) }`
   */
  @MainThread
  suspend fun send(event: E) {
    debugCheckImmediateMainDispatcher()

    channel.trySend(event)
      .onFailure {}
      .onSuccess {}
  }

  override fun close() {
    channel.close()
  }
}
