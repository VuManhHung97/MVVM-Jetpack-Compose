package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import kotlinx.coroutines.flow.Flow

interface ForceUpdateErrorEventDataSource {
  val events: Flow<Unit>
}

interface ForceUpdateErrorEventEmit {
  fun emitEvent()
}
