package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import kotlinx.coroutines.flow.Flow

interface UnauthorizedErrorEventDataSource {
  val events: Flow<Unit>
}

interface UnauthorizedErrorEventEmit {
  fun emitEvent()
}
