package com.vmh.mvvmjetpackcompose.core.network.remote.datasourceimpl

import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UnauthorizedErrorEventDataSource
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UnauthorizedErrorEventEmit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Singleton
internal class RealUnauthorizedErrorEventHandler @Inject constructor() :
  UnauthorizedErrorEventDataSource,
  UnauthorizedErrorEventEmit {
  private val _event = Channel<Unit>(Channel.UNLIMITED)

  override val events: Flow<Unit>
    get() = _event.receiveAsFlow()

  override fun emitEvent() {
    check(_event.trySend(Unit).isSuccess) { "Cannot send event" }
  }
}
