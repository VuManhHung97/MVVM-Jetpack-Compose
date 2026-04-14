package com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.ForceUpdateErrorEventDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.ForceUpdateErrorEventEmit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Singleton
internal class RealForceUpdateErrorEventHandler @Inject constructor() :
  ForceUpdateErrorEventDataSource,
  ForceUpdateErrorEventEmit {
  private val _event = Channel<Unit>(Channel.UNLIMITED)

  override val events: Flow<Unit>
    get() = _event.receiveAsFlow()

  override fun emitEvent() {
    check(_event.trySend(Unit).isSuccess) { "Cannot send event" }
  }
}
